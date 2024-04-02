package dev.marfien.rewibw.game.lobby.listeners;

import dev.marfien.rewibw.RewiBWPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final Location spawn;

    @EventHandler
    private void onPlayerSpawn(PlayerSpawnLocationEvent event) {
        event.setSpawnLocation(this.spawn);
    }

    @EventHandler
    private void onMoveBelowY(PlayerMoveEvent event) {
        if (event.getTo().getY() < 0) {
            event.getPlayer().teleport(this.spawn);
        }
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        Player player = event.getEntity();
        Bukkit.getScheduler().runTaskLater(RewiBWPlugin.getInstance(), () -> player.spigot().respawn(), 10);
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {
        event.setFormat(event.getPlayer().getDisplayName() + " §8» §f" + event.getMessage());
    }

    @EventHandler
    private void onHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onExp(PlayerExpChangeEvent event) {
        event.setAmount(0);
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onInteract(PlayerInteractAtEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onInventoryInteract(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onItemPickup(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

}
