package dev.marfien.rewibw.team;

import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.playing.PlayingGameState;
import dev.marfien.rewibw.scoreboard.CustomScoreboardManager;
import dev.marfien.rewibw.scoreboard.ScoreboardObjective;
import dev.marfien.rewibw.scoreboard.ScoreboardTeam;
import dev.marfien.rewibw.shared.Position;
import dev.marfien.rewibw.shared.TeamColor;
import dev.marfien.rewibw.shared.config.MapConfig;
import dev.marfien.rewibw.world.MapWorld;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Setter
@Getter
public class GameTeam {

    private final TeamColor color;
    private final ItemStack[] armor = new ItemStack[]{
            new ItemStack(Material.LEATHER_BOOTS),
            new ItemStack(Material.LEATHER_LEGGINGS),
            new ItemStack(Material.LEATHER_CHESTPLATE),
            new ItemStack(Material.LEATHER_HELMET)
    };

    private final List<Player> members = new ArrayList<>();
    private final TeamMemberDisplay[] memberDisplays;
    private final ScoreboardTeam playerScoreboardTeam;
    private final ScoreboardTeam displayScoreboardTeam;
    private final Inventory teamChest;

    private Location spawn;
    private TeamBed bed;

    public GameTeam(TeamColor color, World lobby, Position... displayLocations) {
        this.color = color;
        this.playerScoreboardTeam = CustomScoreboardManager.registerTeam("team_" + color.name());
        this.playerScoreboardTeam.setPrefix(ignored -> color.getChatColor().toString());
        this.displayScoreboardTeam = CustomScoreboardManager.registerTeam("display_" + color.name());
        this.displayScoreboardTeam.addEntry(this.getScoreboardEntry(true));
        this.displayScoreboardTeam.addEntry(this.getScoreboardEntry(false));

        this.teamChest = Bukkit.createInventory(null, 27, "Team Inventar");

        LeatherArmorMeta meta = (LeatherArmorMeta) Bukkit.getItemFactory().getItemMeta(Material.LEATHER_BOOTS);
        meta.setColor(this.color.getDyeColor().getColor());
        for (ItemStack item : this.armor) {
            item.setItemMeta(meta);
        }

        this.memberDisplays = new TeamMemberDisplay[displayLocations.length];
        for (int i = 0; i < displayLocations.length; i++) {
            this.memberDisplays[i] = new TeamMemberDisplay(this, displayLocations[i].toLocation(lobby));
        }
    }

    public ItemStack getBoots() {
        return this.armor[0];
    }

    public ItemStack getLeggings() {
        return this.armor[1];
    }

    public ItemStack getChestplate() {
        return this.armor[2];
    }

    public ItemStack getHelmet() {
        return this.armor[3];
    }

    public void init(MapWorld mapWorld) {
        World world = mapWorld.getWorld();
        MapConfig.MapTeamConfig config = mapWorld.getConfig().getTeams().get(this.color);
        this.spawn = config.getSpawn().toLocation(world);
        MapConfig.TeamBedConfig bedConfig = config.getBed();
        this.setBed(new TeamBed(this, world.getBlockAt(bedConfig.getX(), bedConfig.getY(), bedConfig.getZ()), bedConfig.getDirection()));
    }

    public void setBed(TeamBed bed) {
        if (this.bed != null) {
            HandlerList.unregisterAll(this.bed);
        }
        this.bed = bed;
        RewiBWPlugin.getPluginLogger().info("Set bed for team {} at {}", this.color, bed.getFirstBedBlock());
        this.displayScoreboardTeam.setPrefix(player -> (this.bed.isAlive() ? ChatColor.RED : ChatColor.GRAY) + "❤ ");
        Bukkit.getPluginManager().registerEvents(bed, RewiBWPlugin.getInstance());
    }

    void addMember(Player player) {
        this.members.add(player);
        this.playerScoreboardTeam.addEntry(player.getName());
        player.setDisplayName(this.color.getChatColor() + player.getName());
        this.getEmptyDisplay().ifPresent(memberDisplay -> memberDisplay.setPlayer(player));

        this.updateScoreboardEntry();
    }

    void removeMember(Player player) {
        this.members.remove(player);
        this.playerScoreboardTeam.removeEntry(player.getName());
        player.setDisplayName(player.getName());
        this.getDisplayOfPlayer(player).ifPresent(TeamMemberDisplay::removePlayer);

        this.updateScoreboardEntry();
    }

    public Optional<TeamMemberDisplay> getEmptyDisplay() {
        return this.getDisplayOfPlayer(null);
    }

    public Optional<TeamMemberDisplay> getDisplayOfPlayer(Player player) {
        for (TeamMemberDisplay memberDisplay : this.memberDisplays) {
            if (memberDisplay.getPlayer() == player)
                return Optional.of(memberDisplay);
        }

        return Optional.empty();
    }

    public void updateScoreboardEntry() {
        ScoreboardObjective objective = PlayingGameState.getSidebarObjective();
        if (objective != null) {
            objective.setScore(this.getScoreboardEntry(), this.size());
        }
    }

    public String getScoreboardEntry() {
        return this.getScoreboardEntry(this.bed.isAlive());
    }

    public String getScoreboardEntry(boolean bedAlive) {
        return (bedAlive ? ChatColor.DARK_BLUE : ChatColor.BLACK) + this.color.getDisplayName();
    }

    public int size() {
        return this.members.size();
    }

    public boolean isFull() {
        return this.members.size() >= RewiBWPlugin.getPluginConfig().getTeams().getPlayersPerTeam();
    }

    public boolean isMember(Player player) {
        return this.members.contains(player);
    }
}
