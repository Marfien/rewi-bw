package dev.marfien.rewibw.game;

import dev.marfien.rewibw.RewiBWPlugin;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;

public class GameStateManager {

    private static final Logger LOGGER = LogManager.getLogger();

    @Getter
    private static GameState activeGameState;

    @SneakyThrows
    public static void setActiveGameState(GameState state) {
        if (Bukkit.isPrimaryThread()) {
            setActiveGameStateSync(state);
        } else {
            Bukkit.getScheduler().callSyncMethod(RewiBWPlugin.getInstance(), () -> {
                setActiveGameStateSync(state);
                return null;
            }).get();
        }
    }

    private static void setActiveGameStateSync(GameState state) {
        LOGGER.info("Setting active game state to " + state.getClass().getSimpleName());
        if (hasActiveGameState()) {
            activeGameState.stop();
        }

        activeGameState = state;
        state.start();
        System.gc();
    }

    public static boolean hasActiveGameState() {
        return activeGameState != null;
    }

}
