package dev.marfien.rewibw.game.lobby.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

@RequiredArgsConstructor
public class LobbyWorldListener implements Listener {

    private final Location spawn;

    @EventHandler
    private void onPlayerSpawn(PlayerSpawnLocationEvent event) {
        event.setSpawnLocation(spawn);
    }

    @EventHandler
    private void onExplosion(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onExplosion(BlockExplodeEvent event) {
        event.setCancelled(true);
    }

}
