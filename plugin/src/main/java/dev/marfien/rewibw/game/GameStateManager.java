package dev.marfien.rewibw.game;

import lombok.Getter;

public class GameStateManager {

    @Getter
    private static GameState activeGameState;

    public static void setActiveGameState(GameState state) {
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
