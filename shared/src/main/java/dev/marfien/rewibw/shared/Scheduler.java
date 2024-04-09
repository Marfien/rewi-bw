package dev.marfien.rewibw.shared;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@RequiredArgsConstructor
public class Scheduler {
    
    private static final BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
    private final Plugin plugin;

    public <T> Future<T> callSyncMethod(Callable<T> task) {
        return bukkitScheduler.callSyncMethod(this.plugin, task);
    }

    public <T> T awaitSyncMethod(Callable<T> task) throws Exception {
        if (Bukkit.isPrimaryThread()) {
            return task.call();
        } else {
            return callSyncMethod(task).get();
        }
    }

    public void cancelTasks() {
        bukkitScheduler.cancelTasks(this.plugin);
    }

    public void cancelAllTasks() {
        bukkitScheduler.cancelAllTasks();
    }

    public List<BukkitWorker> getActiveWorkers() {
        return bukkitScheduler.getActiveWorkers();
    }

    public List<BukkitTask> getPendingTasks() {
        return bukkitScheduler.getPendingTasks();
    }

    public BukkitTask runTask(Runnable task) throws IllegalArgumentException {
        return bukkitScheduler.runTask(this.plugin, task);
    }

    public BukkitTask runTaskAsynchronously(Runnable task) throws IllegalArgumentException {
        return bukkitScheduler.runTaskAsynchronously(this.plugin, task);
    }

    public BukkitTask runTaskLater(Runnable task, long delay) throws IllegalArgumentException {
        return bukkitScheduler.runTaskLater(this.plugin, task, delay);
    }

    public BukkitTask runTaskLaterAsynchronously(Runnable task, long delay) throws IllegalArgumentException {
        return bukkitScheduler.runTaskLaterAsynchronously(this.plugin, task, delay);
    }

    public BukkitTask runTaskTimer(Runnable task, long delay, long period) throws IllegalArgumentException {
        return bukkitScheduler.runTaskTimer(this.plugin, task, delay, period);
    }

    public BukkitTask runTaskTimerAsynchronously(Runnable task, long delay, long period) throws IllegalArgumentException {
        return bukkitScheduler.runTaskTimerAsynchronously(this.plugin, task, delay, period);
    }
}
