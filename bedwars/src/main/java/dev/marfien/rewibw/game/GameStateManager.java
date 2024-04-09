package dev.marfien.rewibw.game;

import dev.marfien.rewibw.RewiBWPlugin;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;

@UtilityClass
public class GameStateManager {

    private static final Logger LOGGER = LogManager.getLogger();

    @Getter
    private static GameState activeGameState;

    @SneakyThrows
    public static void setActiveGameState(GameState state) {
        if (Bukkit.isPrimaryThread()) {
            setActiveGameStateSync(state);
        } else {
            RewiBWPlugin.getScheduler().awaitSyncMethod(() -> {
                setActiveGameStateSync(state);
                return null;
            });
        }
    }

    private static void setActiveGameStateSync(GameState state) {
        if (hasActiveGameState()) {
            activeGameState.stop();
        }

        if (state == null) {
            activeGameState = null;
            return;
        }

        LOGGER.info("Setting active game state to {}", state.getClass().getSimpleName());
        activeGameState = state;
        state.start();
        System.gc();
    }

    public static boolean hasActiveGameState() {
        return activeGameState != null;
    }

}
