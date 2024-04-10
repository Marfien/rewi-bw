package dev.marfien.rewibw.game.end;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.AbstractCountdown;
import dev.marfien.rewibw.game.GameStateManager;
import dev.marfien.rewibw.game.playing.PlayingGameState;

public class EndCountdown extends AbstractCountdown {

    public EndCountdown() {
        super(30);
    }

    @Override
    public void onStart() {
        // Do nothing
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
        int minutes = second / 60;
        int seconds = second % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);
        PlayingGameState.getSidebarObjective().setDisplayName("§3BedWars §7- §b" + timeString);
    }
}
