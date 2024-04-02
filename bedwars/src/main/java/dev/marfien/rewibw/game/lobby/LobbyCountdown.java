package dev.marfien.rewibw.game.lobby;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.AbstractCountdown;
import dev.marfien.rewibw.game.GameStateManager;
import dev.marfien.rewibw.game.playing.PlayingGameState;
import dev.marfien.rewibw.voting.MapVoting;
import dev.marfien.rewibw.world.MapPool;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class LobbyCountdown extends AbstractCountdown {

    private static final int INIT_SECONDS = 60;

    private BukkitTask idleTask;

    public LobbyCountdown() {
        super(INIT_SECONDS);
    }

    public void startIdle() {
        this.idleTask = Bukkit.getScheduler().runTaskTimerAsynchronously(RewiBWPlugin.getInstance(), () ->
                Message.broadcast(
                        RewiBWPlugin.PREFIX + Message.LOBBY_IDLE.format(
                                RewiBWPlugin.getConfig().getTeams().getMinPlayers() - Bukkit.getOnlinePlayers().size()
                        )
                ), 0, 20 * 30);
    }

    public void stopIdle() {
        if (this.idleTask == null) return;

        this.idleTask.cancel();
    }

    public boolean isIdleRunning() {
        return this.idleTask != null;
    }

    @Override
    public void onStart() {
        this.stopIdle();
        Message.broadcast("\n " + RewiBWPlugin.PREFIX + Message.COUNTDOWN_BEGAN + "\n ");
        MapVoting.start();
    }

    @Override
    public void onStop() { }

    @SneakyThrows
    @Override
    public void onSecond(int second) {
        float exp = 0.99F * second / INIT_SECONDS;
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setLevel(second);
            player.setExp(exp);
        }

        switch (second) {
            case 10:
                MapVoting.getOrChooseWinner();
            case 60:
            case 30:
            case 5:
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(RewiBWPlugin.PREFIX + Message.GAME_STARTS_IN.format(second));
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                }
                break;
            case 0:
                GameStateManager.setActiveGameState(new PlayingGameState(MapVoting.getWinner()));
                break;
        }
    }
}
