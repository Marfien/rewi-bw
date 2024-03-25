package dev.marfien.rewibw.game.lobby;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.AbstractCountdown;
import dev.marfien.rewibw.game.GameStateManager;
import dev.marfien.rewibw.game.playing.PlayingGameState;
import dev.marfien.rewibw.team.TeamManager;
import dev.marfien.rewibw.voting.MapVoting;
import dev.marfien.rewibw.world.MapPool;
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
        this.idleTask = new BukkitRunnable() {
            @Override
            public void run() {
                    Message.broadcast(RewiBWPlugin.PREFIX + Message.LOBBY_IDLE.format(RewiBWPlugin.getMinPlayers() - Bukkit.getOnlinePlayers().size()));
            }
        }.runTaskTimerAsynchronously(RewiBWPlugin.getInstance(), 0, 20 * 30);
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

    @Override
    public void onSecond(int second) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setLevel(second);
            float exp = 0.99F * second / INIT_SECONDS;
            player.setExp(exp);
        }

        switch (second) {
            case 10:
                CompletableFuture.supplyAsync(() -> {
                            try {
                                return MapPool.requestMap(MapVoting.getOrChooseWinner());
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.exit(1);
                                return null;
                            }
                        })
                        .thenAccept(PlayingGameState::setMap);
            case 60:
            case 30:
            case 5:
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(RewiBWPlugin.PREFIX + Message.GAME_STARTS_IN.format(second));
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
                }
                break;
            case 0:
                GameStateManager.setActiveGameState(PlayingGameState.getInstance());
                break;
        }
    }
}
