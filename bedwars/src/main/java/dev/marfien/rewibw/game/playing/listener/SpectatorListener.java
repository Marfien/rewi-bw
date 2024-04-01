package dev.marfien.rewibw.game.playing.listener;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.PlayerManager;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.playing.PlayingGameState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class SpectatorListener implements Listener {

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        PlayerManager.setSpectator(player);
        PlayerManager.hideSpectators(player);
    }

    @EventHandler
    private void onSpawn(PlayerSpawnLocationEvent event) {
        event.setSpawnLocation(PlayingGameState.getMap().getSpawn());
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        if (!PlayerManager.isSpectator(event.getPlayer())) return;

        event.setQuitMessage(null);
        PlayerManager.removeSpectator(event.getPlayer());
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!PlayerManager.isSpectator(player)) return;

        event.setCancelled(true);
        PlayerManager.forAllSpectators(member -> member.sendMessage("§8[§7SPECTATOR§8] §7" + player.getName() + "§8 » §f" + event.getMessage()));
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!PlayerManager.isSpectator((Player) event.getEntity())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        if (!PlayerManager.isSpectator(event.getPlayer())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        if (!PlayerManager.isSpectator(event.getPlayer())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!PlayerManager.isSpectator(event.getPlayer())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (!PlayerManager.isSpectator((Player) event.getWhoClicked())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onItemPickUp(PlayerPickupItemEvent event) {
        if (!PlayerManager.isSpectator(event.getPlayer())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onInteractWithPlayer(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity interacted = event.getRightClicked();

        if (!(interacted instanceof Player)) return;
        Player rightClicked = (Player) interacted;

        if (!PlayerManager.isSpectator(player)) return;
        if (PlayerManager.isSpectator(rightClicked)) return;

        ((CraftPlayer) player).getHandle().setSpectatorTarget(((CraftPlayer) rightClicked).getHandle());
        player.sendMessage(RewiBWPlugin.PREFIX + Message.SPECTATOR_TARGET.format(rightClicked.getDisplayName()));
        player.sendTitle("§6Spieleransicht", "§6Drücke SHIFT zum verlassen");
    }

}
