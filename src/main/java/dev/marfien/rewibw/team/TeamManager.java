package dev.marfien.rewibw.team;

import com.google.common.base.Preconditions;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.GameStateManager;
import dev.marfien.rewibw.game.lobby.LobbyGameState;
import dev.marfien.rewibw.game.playing.PlayingGameState;
import dev.marfien.rewibw.game.playing.item.SpectatorCompass;
import dev.marfien.rewibw.scoreboard.CustomScoreboardManager;
import dev.marfien.rewibw.scoreboard.ScoreboardTeam;
import dev.marfien.rewibw.world.GameWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;
import java.util.logging.Level;

public class TeamManager {

    private static final Collection<GameTeam> teams = new HashSet<>();

    private static final Map<Player, GameTeam> playerTeamMap = new HashMap<>();

    public static Collection<Listener> init(GameWorld lobby) {
        Bukkit.getPluginManager().registerEvents(new TeamListener(), RewiBWPlugin.getInstance());

        return initTeams(lobby);
    }

    private static Collection<Listener> initTeams(GameWorld lobby) {
        Collection<Listener> listeners = new ArrayList<>();

        for (TeamColor teamColor : RewiBWPlugin.getTeams()) {
            String key = teamColor.name().toLowerCase();
            Location teamJoiner = lobby.getLocation("teams." + key + ".joiner");
            List<Location> displayLocations = lobby.getLocationList("teams." + key + ".memberDisplay");

            if (displayLocations.size() < RewiBWPlugin.getPlayersPerTeam())
                throw new IllegalArgumentException("The memberDisplay section needs to be at least the size of players per team");

            GameTeam team = new GameTeam(teamColor, displayLocations.toArray(new Location[0]));
            listeners.add(new TeamJoiner(teamJoiner, team));
            teams.add(team);
            RewiBWPlugin.getInstance().getLogger().log(Level.INFO, "Registered team " + teamColor.name() + " with " + displayLocations.size() + " display locations");
        }

        return listeners;
    }

    public static void assignTeams() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (hasTeam(player)) continue;
            assignTeam(player);
        }
    }

    private static void assignTeam(Player player) {
        GameTeam team = getSmallestTeam();
        if (tryJoinTeam(player, team)) {
            player.sendMessage(RewiBWPlugin.PREFIX + "Du wurdest dem Team " + team.getColor().getChatColor() + team.getColor().getName() + "§7 zugewiesen.");
            return;
        }
        player.kickPlayer("Es konnte kein Team für dich gefunden werden.");
    }

    private static GameTeam getSmallestTeam() {
        GameTeam smallest = null;
        for (GameTeam team : teams) {
            if (smallest == null || team.getMembers().size() < smallest.getMembers().size()) {
                smallest = team;
            }
        }
        return smallest;
    }

    public static boolean tryJoinTeam(Player player, GameTeam team) {
        if (team.isFull()) {
            return false;
        }
        setTeam(player, team);
        return true;
    }

    public static void setTeam(Player player, GameTeam team) {
        Preconditions.checkNotNull(team, "Team cannot be null");

        GameTeam oldTeam = playerTeamMap.put(player, team);
        if (oldTeam != null) {
            oldTeam.removeMember(player);
        }

        team.addMember(player);

        if (GameStateManager.getActiveGameState() == LobbyGameState.getInstance()) {
            player.getInventory().setArmorContents(Arrays.copyOf(team.getArmor(), 3));
        }
        SpectatorCompass.refreshInventory();
    }

    public static void removeTeam(Player player) {
        GameTeam team = playerTeamMap.remove(player);
        if (team != null) {
            player.getInventory().setArmorContents(new ItemStack[4]);
            team.removeMember(player);
        }
        SpectatorCompass.refreshInventory();
    }

    public static GameTeam getTeam(Player player) {
        return playerTeamMap.get(player);
    }

    public static boolean hasTeam(Player player) {
        return playerTeamMap.containsKey(player);
    }

    public static Collection<GameTeam> getTeams() {
        return Collections.unmodifiableCollection(teams);
    }

    public static void broadcastTeams() {
        Collection<String> teamStrings = new ArrayList<>();
        for (GameTeam team : teams) {
            if (team.getMembers().isEmpty()) continue;
            teamStrings.add(team.getColor().getChatColor() + "Team " + team.getColor().getName() + "§7: §f" + team.getMembers().stream().map(Player::getName).reduce((a, b) -> a + ", " + b).orElse(""));
        }

        String[] messages = teamStrings.toArray(new String[0]);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(" \n§f§m-----------§r §3Teams §f§m-----------§r");
            player.sendMessage(messages);
            player.sendMessage(" ");
        }
    }

    private static class TeamListener implements Listener {

        @EventHandler(priority = EventPriority.HIGHEST)
        private void onPlayerQuit(PlayerQuitEvent event) {
            removeTeam(event.getPlayer());
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
        private void onTeamMemberDamage(EntityDamageByEntityEvent event) {
            if (!(event.getEntity() instanceof Player)) return;

            Entity originalDamager = event.getDamager();
            if (originalDamager instanceof Projectile) {
                ProjectileSource shooter = ((Projectile) originalDamager).getShooter();
                if (shooter instanceof Entity) {
                    originalDamager = (Entity) shooter;
                }
            }

            if (!(originalDamager instanceof Player)) return;

            Player damager = (Player) originalDamager;
            Player damaged = (Player) event.getEntity();

            if (damager == damaged) return;

            if (getTeam(damager) == getTeam(damaged)) {
                event.setCancelled(true);
            }
        }

    }

}
