package dev.marfien.rewibw.game.lobby.listeners;

import dev.marfien.rewibw.world.GameWorld;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

@RequiredArgsConstructor
public class LobbyWorldListener implements Listener {

    private final GameWorld gameWorld;

    @EventHandler
    private void onPlayerSpawn(PlayerSpawnLocationEvent event) {
        event.setSpawnLocation(this.gameWorld.getSpawn());
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
