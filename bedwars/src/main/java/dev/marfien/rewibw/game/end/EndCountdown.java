package dev.marfien.rewibw.game.end;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.AbstractCountdown;
import dev.marfien.rewibw.game.GameStateManager;

public class EndCountdown extends AbstractCountdown {

    public EndCountdown() {
        super(30);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {
        GameStateManager.setActiveGameState(null);
    }

    @Override
    public void onSecond(int second) {
        if (second % 10 == 0 || second <= 5) {
            Message.broadcast(RewiBWPlugin.PREFIX + Message.SERVER_STOPPING.format(second));
        }
    }
}
