package dev.marfien.rewibw.game;

import dev.marfien.rewibw.RewiBWPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

@Getter
@RequiredArgsConstructor
public abstract class AbstractCountdown implements Countdown {

    private final int initSeconds;
    @Setter
    private int seconds = -1;

    private BukkitTask task;

    public final void start() {
        if (this.isRunning()) {
            throw new IllegalStateException("Cannot start running countdown");
        }
        this.onStart();

        this.seconds = this.initSeconds;
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                onSecond(seconds);

                if (seconds == 0) {
                    stop();
                }

                seconds--;
            }
        }.runTaskTimerAsynchronously(RewiBWPlugin.getInstance(), 0, 20);
    }

    public final void stop() {
        if (this.task == null) return;

        this.task.cancel();
        this.task = null;
        this.onStop();
    }

    public boolean isRunning() {
        return this.task != null;
    }

    public abstract void onStart();

    public abstract void onStop();

    public abstract void onSecond(int second);

}
