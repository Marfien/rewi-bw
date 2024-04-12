package dev.marfien.rewibw.game.playing.listener;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.PlayerManager;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.util.LazyValue;
import dev.marfien.rewibw.world.MapWorld;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.LazyInitVar;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class SpectatorListener implements Listener {

    private final LazyValue<Location> spectatorSpawn;

    public SpectatorListener(Supplier<Location> spectatorSpawn) {
        this.spectatorSpawn = new LazyValue<>(spectatorSpawn);
    }

    @EventHandler
    private void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!PlayerManager.isSpectator((Player) event.getEntity())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        PlayerManager.setSpectator(player);
        PlayerManager.hideSpectators(player);
    }

    @EventHandler
    private void onExp(PlayerExpChangeEvent event) {
        if (!PlayerManager.isSpectator(event.getPlayer())) return;

        event.setAmount(0);
    }

    @EventHandler
    private void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        if (!PlayerManager.isSpectator((Player) event.getEntity())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onSpawn(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();
        player.setAllowFlight(true);
        player.setFlying(true);
        event.setSpawnLocation(this.spectatorSpawn.get());
    }

    @EventHandler
    private void onRespawn(PlayerRespawnEvent event) {
        if (!PlayerManager.isSpectator(event.getPlayer())) return;

        event.setRespawnLocation(this.spectatorSpawn.get());
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        if (!PlayerManager.isSpectator(event.getPlayer())) return;

        event.setQuitMessage(null);
        PlayerManager.removeSpectator(event.getPlayer());
    }

    @EventHandler
    private void onMove(PlayerMoveEvent event) {
        if (!PlayerManager.isSpectator(event.getPlayer())) return;

        if (event.getTo().getY() < -64) {
            event.getPlayer().teleport(this.spectatorSpawn.get());
        }
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!PlayerManager.isSpectator(player)) return;

        event.setCancelled(true);
        String message = "§8[§7SPECTATOR§8] §7" + player.getName() + "§8 » §f" + event.getMessage();
        PlayerManager.forAllSpectators(member -> member.sendMessage(message));
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
        if (!PlayerManager.isSpectator(event.getPlayer())) return;
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

    @EventHandler
    private void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!PlayerManager.isSpectator(player)) return;

        if (!event.isSneaking() && player.getSpectatorTarget() != null) {
            ((CraftPlayer) player).getHandle().setSpectatorTarget(null);
            player.sendMessage(RewiBWPlugin.PREFIX + Message.SPECTATOR_TARGET.format("keiner"));
        }
    }

}
