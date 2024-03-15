package dev.marfien.rewibw.game.end;

import dev.marfien.rewibw.game.GameState;
import lombok.Getter;
import org.bukkit.event.Listener;

public class EndGameState extends GameState {

    @Getter
    private static final EndGameState instance = new EndGameState();

    @Override
    public Listener[] getListeners() {
        return new Listener[0];
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}
