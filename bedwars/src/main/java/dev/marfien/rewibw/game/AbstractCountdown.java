package dev.marfien.rewibw.game;

import dev.marfien.rewibw.RewiBWPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@RequiredArgsConstructor
public abstract class AbstractCountdown implements Countdown {

    private static final Logger LOGGER = RewiBWPlugin.getPluginLogger();

    private final int initSeconds;
    private int seconds = -1;

    private BukkitTask task;

    public final void start() {
        if (this.isRunning()) {
            throw new IllegalStateException("Cannot start running countdown");
        }

        LOGGER.log(Level.INFO, "Starting countdown {0} with {1} seconds", new Object[]{this.getClass(), this.initSeconds});
        this.onStart();

        this.seconds = this.initSeconds;
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(RewiBWPlugin.getInstance(), () -> {
            LOGGER.log(Level.FINEST, "Countdown {0} running with {1} seconds", new Object[]{this.getClass(), this.seconds});
            this.onSecond(this.seconds);

            if (this.seconds == 0) {
                stop();
            }

            this.seconds--;
        }, 0, 20);
    }

    public final void stop() {
        if (this.task == null) return;

        this.task.cancel();
        this.task = null;
        this.onStop();
        LOGGER.info("Stopping countdown " + this.getClass());
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
        LOGGER.log(Level.INFO, "Setting countdown {0} to {1} seconds", new Object[]{this.getClass(), seconds});
    }

    public boolean isRunning() {
        return this.task != null;
    }

    public abstract void onStart();

    public abstract void onStop();

    public abstract void onSecond(int second);

}
