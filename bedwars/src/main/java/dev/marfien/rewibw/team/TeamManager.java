package dev.marfien.rewibw.team;

import com.google.common.base.Preconditions;
import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.GameStateManager;
import dev.marfien.rewibw.game.lobby.LobbyGameState;
import dev.marfien.rewibw.game.lobby.LobbyWorld;
import dev.marfien.rewibw.game.lobby.listeners.TeamJoinerListener;
import dev.marfien.rewibw.shared.Position;
import dev.marfien.rewibw.shared.TeamColor;
import dev.marfien.rewibw.shared.config.LobbyConfig;
import dev.marfien.rewibw.shared.config.PluginConfig;
import org.bukkit.Bukkit;
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

    public static void init() {
        Bukkit.getPluginManager().registerEvents(new TeamListener(), RewiBWPlugin.getInstance());
    }

    public static void initTeams(LobbyWorld world, TeamJoinerListener teamJoinerListener) {
        PluginConfig.TeamConfig teamConfig = RewiBWPlugin.getPluginConfig().getTeams();
        Map<TeamColor, LobbyConfig.LobbyTeamConfig> teams = world.getConfig().getTeams();

        for (TeamColor teamColor : teamConfig.getVariants()) {
            LobbyConfig.LobbyTeamConfig lobbyTeamConfig = teams.get(teamColor);
            Position[] displayLocations = lobbyTeamConfig.getDisplays();

            if (displayLocations.length < teamConfig.getPlayersPerTeam())
                throw new IllegalArgumentException("The memberDisplay section needs to be at least the size of players per team");

            GameTeam team = new GameTeam(teamColor, world.getWorld(), displayLocations);

            teamJoinerListener.addJoiner(lobbyTeamConfig.getJoiner().toLocation(world.getWorld()), team);

            TeamManager.teams.add(team);
            RewiBWPlugin.getInstance().getLogger().log(Level.INFO, "Registered team " + teamColor.name() + " with " + displayLocations.length + " display locations");
        }
    }

    public static void assignTeams() {
        GameTeam[] teams = getTeamsSortedBySize();
        int teamIndex = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (hasTeam(player)) continue;

            while(!tryJoinTeam(player, teams[teamIndex])) {
                teamIndex++;

                if (teamIndex >= teams.length) {
                    player.kickPlayer(Message.NO_TEAM_FOUND.toString());
                    return;
                }
            }
            player.sendMessage(RewiBWPlugin.PREFIX + Message.TEAM_ASSIGNED.format(teams[teamIndex].getColor().getDisplayName()));
        }
    }

    private static GameTeam[] getTeamsSortedBySize() {
        return teams.stream()
                .sorted(Comparator.comparingInt(GameTeam::size))
                .toArray(GameTeam[]::new);
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

        if (GameStateManager.getActiveGameState() instanceof LobbyGameState) {
            player.getInventory().setArmorContents(Arrays.copyOf(team.getArmor(), 3));
        }
    }

    public static void removeTeam(Player player) {
        GameTeam team = playerTeamMap.remove(player);
        if (team == null) return;

        player.getInventory().setArmorContents(new ItemStack[4]);
        team.removeMember(player);
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
        Message.broadcast(Message.TEAM_BROADCAST_HEADER.toString());
        for (GameTeam team : teams) {
            if (team.getMembers().isEmpty()) continue;
            TeamColor color = team.getColor();
            Message.broadcast(Message.TEAM_BROADCAST_FORMAT.format(color.getChatColor(), color.getName(),
                    team.getMembers().stream().map(Player::getName).reduce((a, b) -> a + ", " + b).get()));
        }
        Message.broadcast(Message.TEAM_BROADCAST_HEADER.toString());
    }

    public static Collection<Player> getPlayersWithTeam() {
        return playerTeamMap.keySet();
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
