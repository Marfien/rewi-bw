package dev.marfien.rewibw.game.lobby;

import dev.marfien.rewibw.Message;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JumpAndRun {

    private final Map<Player, Long> records = new HashMap<>();
    private final Map<Player, Long> startTime = new HashMap<>();

    private final Location start;
    private final Location finish;
    private final JumpAndRunListener listener = new JumpAndRunListener();

    public void start(Player player) {
        this.startTime.put(player, System.currentTimeMillis());
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
    }

    public void finish(Player player) {
        Long started = this.startTime.get(player);
        if (started == null) return;

        long time = System.currentTimeMillis() - started;
        Long previousRecord = this.records.get(player);
        String timeString = String.format("%.2fs", time / 1000F);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        player.sendMessage(Message.JUMP_AND_RUN_FINISH.format(String.valueOf(time)));
        if (previousRecord == null || time < previousRecord) {
            player.sendMessage(Message.JUMP_AND_RUN_NEW_RECORD.format(timeString));
            this.records.put(player, time);
        }
    }

    public void reset(Player player) {
        this.records.remove(player);
        this.startTime.remove(player);
    }

    private class JumpAndRunListener implements Listener {

        @EventHandler
        private void onJumpAndRunStart(PlayerMoveEvent event) {
            if (event.getTo().distanceSquared(start) < 0.5) {
                start(event.getPlayer());
            }
        }

        @EventHandler
        private void onJumpAndRunFinish(PlayerMoveEvent event) {
            if (event.getTo().distanceSquared(finish) < 0.5) {
                finish(event.getPlayer());
            }
        }

        @EventHandler
        private void onQuit(PlayerMoveEvent event) {
            reset(event.getPlayer());
        }

    }

}
