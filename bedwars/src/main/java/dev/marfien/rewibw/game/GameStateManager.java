package dev.marfien.rewibw.game;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameStateManager {

    private static final Logger LOGGER = LogManager.getLogger();

    @Getter
    private static GameState activeGameState;

    public static void setActiveGameState(GameState state) {
        LOGGER.info("Setting active game state to " + state.getClass().getSimpleName());
        if (hasActiveGameState()) {
            activeGameState.stop();
        }

        activeGameState = state;
        state.start();
    }

    public static boolean hasActiveGameState() {
        return activeGameState != null;
    }

}
