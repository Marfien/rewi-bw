package dev.marfien.rewibw.game.playing.listener;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.PlayerManager;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.GameStateManager;
import dev.marfien.rewibw.game.end.EndGameState;
import dev.marfien.rewibw.game.playing.item.SpectatorCompass;
import dev.marfien.rewibw.perk.PerkManager;
import dev.marfien.rewibw.team.GameTeam;
import dev.marfien.rewibw.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onVoid(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getNoDamageTicks() > 0) return;

        if (event.getTo().getY() < -64) {
            Player killer = player.getKiller();
            player.damage(20.0, killer == null ? player : killer);
        }
    }

    @EventHandler
    public void onPreDeath(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (player.getHealth() - event.getFinalDamage() > 0) return;

        GameTeam team = TeamManager.getTeam(player);

        if (team == null) return;
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setArmorContents(null);
        if (team.getBed().isAlive()) return;
        // final death

        Message.broadcast(RewiBWPlugin.PREFIX + getDeathMessage(player));
        removeFromGame(player);
        PlayerManager.setSpectator(player);
        event.setCancelled(true);
        player.sendTitle(
                player.getKiller() != null && player.getKiller() != player
                        ? Message.DEATH_TITLE_KILLED.toString()
                        : Message.DEATH_TITLE.toString(),
                ""
        );

        Player killer = player.getKiller();

        if (killer != null && killer != player) {
            PerkManager.KILL_SOUND_PERK_GROUP.getPerk(player).ifPresent(perk -> {
                Sound sound = perk.getData();
                player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
            });
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!TeamManager.hasTeam(player)) return;

        Message.broadcast(RewiBWPlugin.PREFIX + getDeathMessage(player));
        removeFromGame(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.setDeathMessage(RewiBWPlugin.PREFIX + getDeathMessage(player));
        Bukkit.getScheduler().runTaskLater(RewiBWPlugin.getInstance(), () -> event.getEntity().spigot().respawn(), 20);
    }

    private static void removeFromGame(Player player) {
        GameTeam team = TeamManager.getTeam(player);
        TeamManager.removeTeam(player);
        SpectatorCompass.refreshInventory();
        RewiBWPlugin.getPluginLogger().info("Removed player {} ({}) from game", player.getName(), team.getColor());

        if (team.size() == 0) {
            Message.broadcast(RewiBWPlugin.PREFIX + Message.TEAM_ELIMINATED.format(team.getColor().getDisplayName()));
        }

        GameTeam winner = null;
        Collection<GameTeam> teams = TeamManager.getTeams();
        boolean singleTeamLeft = true;
        for (GameTeam gameTeam : teams) {
            if (gameTeam.size() == 0) continue;

            // If there are more than 1 team left, return
            if (winner != null) {
                singleTeamLeft = false;
                break;
            }
            winner = gameTeam;
        }

        if (singleTeamLeft) {
            GameStateManager.setActiveGameState(new EndGameState(winner));
        } else {
            Message.broadcast(RewiBWPlugin.PREFIX + Message.REMAINING_TEAMS.format(teams.size(), TeamManager.getPlayersWithTeam().size()));
        }
    }

    private static String getDeathMessage(Player player) {
        Player killer = player.getKiller();

        if (killer == null || killer == player) {
            return Message.PLAYER_DIED.format(player.getDisplayName());
        }

        int health = (int) Math.round(killer.getHealth());
        ChatColor color = getColorForHealth(health);

        return Message.PLAYER_KILLED.format(player.getDisplayName(), killer.getDisplayName(), color.toString() + Math.round(health / 2D));
    }

    private static ChatColor getColorForHealth(int health) {
        if (health >= 16) return ChatColor.GREEN;
        if (health >= 13) return ChatColor.YELLOW;
        if (health >= 7) return ChatColor.GOLD;
        if (health >= 3) return ChatColor.RED;

        return ChatColor.DARK_RED;
    }

    @EventHandler
    private void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        GameTeam team = TeamManager.getTeam(player);

        if (team == null) return;

        event.setRespawnLocation(team.getSpawn());
        for (Player other : Bukkit.getOnlinePlayers()) {
            other.hidePlayer(player);
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(RewiBWPlugin.getInstance(), () -> {
            for (Player other : Bukkit.getOnlinePlayers()) {
                other.showPlayer(player);
            }
        }, 20L);
    }

}
