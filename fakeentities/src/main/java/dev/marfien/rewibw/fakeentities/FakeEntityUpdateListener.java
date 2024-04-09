package dev.marfien.rewibw.fakeentities;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class FakeEntityUpdateListener implements Listener {

    private final Plugin plugin;

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> FakeEntityManager.updateView(player), 5L);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        // Check if the player actually moved
        if (from.getWorld() == to.getWorld() && from.distanceSquared(to) > 0.01D) {
            FakeEntityManager.smoothUpdateView(event.getPlayer());
        }

        Player player = event.getPlayer();
        if (player.getHealth() <= 0.0) return;

        Chunk oldChunk = from.getChunk();
        Chunk newChunk = to.getChunk();
        if (oldChunk.getWorld() != newChunk.getWorld() || oldChunk.getX() != newChunk.getX() || oldChunk.getZ() != newChunk.getZ()) {
            FakeEntityManager.updateView(player);
        }
    }

    @EventHandler
    public void onPlayerJoined(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            Player player = event.getPlayer();
            FakeEntityManager.updateView(player);
            FakeEntityManager.smoothUpdateView(player);
        }, 20L * 2);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        FakeEntityManager.updateView(player);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Location location = event.getBlock().getLocation();
        for (FakeEntity mob : FakeEntityManager.getFakeEntities()) {
            Location mobLocation = mob.getLocation();
            if (mobLocation.getWorld() != location.getWorld()) continue;
            double dX = mobLocation.getX() - location.getX();
            double dY = mobLocation.getY() - location.getY();

            if (dX * dX + dY * dY > 0.25D) continue;

            int height = mob.getHeight();
            if (height < 0) {
                height = 0;
            }

            double blockY = location.getY();
            double mobY = mobLocation.getY();
            if (mobY > blockY && mobY < blockY + height) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        for (FakeEntity mob : FakeEntityManager.getFakeEntities()) {
            mob.unloadFor(player);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> FakeEntityManager.updateView(player), 10L);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        for (FakeEntity mob : FakeEntityManager.getFakeEntities()) {
            mob.unloadFor(player);
        }
    }
}
