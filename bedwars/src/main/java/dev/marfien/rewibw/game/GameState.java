package dev.marfien.rewibw.game;

import dev.marfien.rewibw.RewiBWPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class GameState {

    public abstract Listener[] getListeners();

    public final void start() {
        Bukkit.getScheduler().runTask(RewiBWPlugin.getInstance(), () -> {
            this.onStart();
            for (Listener listener : getListeners()) {
                Bukkit.getPluginManager().registerEvents(listener, RewiBWPlugin.getInstance());
            }
        });
    }

    public final void stop() {
        for (Listener listener : this.getListeners()) {
            HandlerList.unregisterAll(listener);
        }
        this.onStop();
    }

    public abstract void onStart();

    public abstract void onStop();

}
