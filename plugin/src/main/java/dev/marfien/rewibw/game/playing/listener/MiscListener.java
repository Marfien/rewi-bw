package dev.marfien.rewibw.game.playing.listener;

import dev.marfien.rewibw.PlayerManager;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.team.GameTeam;
import dev.marfien.rewibw.team.TeamManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MiscListener implements Listener {

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        GameTeam team = TeamManager.getTeam(player);

        if (team == null) return;

        String format = player.getDisplayName() + "§8 » §f%2$s";
        String message = event.getMessage();

        if (message.charAt(0) == '@') {
            format = "§6§lGLOBAL §8︳ §r" + format;
            event.setMessage(message.substring(1));
            event.setFormat(format);
            return;
        }

        event.setCancelled(true);

        for (Player member : team.getMembers()) {
            member.sendMessage(String.format(format, player, message));
        }
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (PlayerManager.isSpectator(player)) return;

        event.setQuitMessage(RewiBWPlugin.PREFIX + player.getDisplayName() + " §7hat das Spiel verlassen.");
    }

}