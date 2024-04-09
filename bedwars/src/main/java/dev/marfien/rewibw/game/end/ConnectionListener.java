package dev.marfien.rewibw.game.end;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

}
