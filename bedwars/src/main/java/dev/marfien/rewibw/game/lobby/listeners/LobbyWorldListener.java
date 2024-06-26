package dev.marfien.rewibw.game.lobby.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class LobbyWorldListener implements Listener {

    @EventHandler
    private void onExplosion(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onExplosion(BlockExplodeEvent event) {
        event.setCancelled(true);
    }

}
