package dev.marfien.rewibw.game;

import dev.marfien.rewibw.RewiBWPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class GameState {

    private static final Logger LOGGER = LogManager.getLogger();

    public abstract Listener[] getListeners();

    public final void start() {
        LOGGER.info("Starting game state " + this.getClass());
        Bukkit.getScheduler().runTask(RewiBWPlugin.getInstance(), () -> {
            this.onStart();
            for (Listener listener : getListeners()) {
                Bukkit.getPluginManager().registerEvents(listener, RewiBWPlugin.getInstance());
            }
            System.gc();
        });
    }

    public final void stop() {
        for (Listener listener : this.getListeners()) {
            HandlerList.unregisterAll(listener);
        }
        this.onStop();
        LOGGER.info("Stopped game state " + this.getClass());
    }

    public abstract void onStart();

    public abstract void onStop();

}
