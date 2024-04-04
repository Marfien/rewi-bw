package dev.marfien.rewibw.game.lobby.listeners;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.lobby.LobbyCountdown;
import dev.marfien.rewibw.game.lobby.LobbyGameState;
import dev.marfien.rewibw.shared.config.PluginConfig;
import dev.marfien.rewibw.util.Items;
import dev.marfien.rewibw.voting.MapVoting;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class PlayerConnectionListener implements Listener {

    private static final ItemStack[] LOBBY_CONTENTS = new ItemStack[4 * 9];

    private PluginConfig.TeamConfig teamConfig = RewiBWPlugin.getPluginConfig().getTeams();
    private final LobbyCountdown countdown;

    static {
        LOBBY_CONTENTS[4] = Items.PERKS_ITEM;
        LOBBY_CONTENTS[8] = Items.QUIT_ITEM;
    }

    @EventHandler
    private void onLogin(AsyncPlayerPreLoginEvent event) {
        if (Bukkit.getOnlinePlayers().size() >= this.teamConfig.getMaxPlayers()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "§cDer Server ist bereits voll.");
        }
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.getInventory().setContents(LOBBY_CONTENTS);
        event.setJoinMessage(Message.LOBBY_JOIN.format(ChatColor.GRAY + player.getDisplayName(), Bukkit.getOnlinePlayers().size()));

        if (Bukkit.getOnlinePlayers().size() >= this.teamConfig.getMinPlayers() && !this.countdown.isRunning()) {
            this.countdown.start();
        }

        if (MapVoting.isRunning()) {
            player.getInventory().setItem(0, Items.VOTE_ITEM);
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        MapVoting.removeVote(player);
        event.setQuitMessage(Message.LOBBY_LEAVE.format(ChatColor.GRAY + player.getDisplayName()));

        int players = Bukkit.getOnlinePlayers().size() - 1;
        if (players < this.teamConfig.getMinPlayers() && this.countdown.isRunning()) {
            this.countdown.stop();
            this.countdown.startIdle();
            MapVoting.reset();
        }
    }

}
