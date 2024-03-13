package dev.marfien.rewibw.game.playing.listener;

import dev.marfien.rewibw.PlayerManager;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.playing.PlayingGameState;
import dev.marfien.rewibw.team.GameTeam;
import dev.marfien.rewibw.team.TeamManager;
import dev.marfien.rewibw.voting.MapVoting;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.PlayerInventory;

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

        Bukkit.broadcastMessage(RewiBWPlugin.PREFIX + getDeathMessage(player));
        TeamManager.removeTeam(player);
        PlayerManager.setSpectator(player);
        event.setCancelled(true);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (TeamManager.hasTeam(player)) return;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(RewiBWPlugin.PREFIX + getDeathMessage(player));
        }
        TeamManager.removeTeam(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.setDeathMessage(RewiBWPlugin.PREFIX + getDeathMessage(player));
        Bukkit.getScheduler().runTaskLater(RewiBWPlugin.getInstance(), () -> {
            event.getEntity().spigot().respawn();
        }, 20);
    }

    private static String getDeathMessage(Player player) {
        Player killer = player.getKiller();

        if (killer == null || killer == player) {
            return player.getDisplayName() + " §7ist gestorben.";
        }

        long health = Math.round(killer.getHealth());
        ChatColor color =
                health >= 16 ? ChatColor.GREEN
                        : health >= 13 ? ChatColor.YELLOW
                        : health >= 7 ? ChatColor.GOLD
                        : health >= 3 ? ChatColor.RED
                        : ChatColor.DARK_RED;

        return player.getDisplayName() + " §7wurde von §7" + killer.getDisplayName() + "§7[§c❤" + color + Math.round(health / 2D) + "§7] getötet.";
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        GameTeam team = TeamManager.getTeam(player);

        event.setRespawnLocation(
                team != null
                        ? team.getSpawn()
                        : PlayingGameState.getMap().getSpawn()
        );
    }

}
