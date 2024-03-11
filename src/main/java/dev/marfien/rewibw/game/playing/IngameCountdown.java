package dev.marfien.rewibw.game.playing;

import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.AbstractCountdown;
import dev.marfien.rewibw.game.GameStateManager;
import dev.marfien.rewibw.game.end.EndGameState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class IngameCountdown extends AbstractCountdown {


    public IngameCountdown() {
        super(60 * 60);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onSecond(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);
        PlayingGameState.getSidebarObjective().setDisplayName("§3BedWars §7- §b" + timeString);

        if (isTimeValidForBroadcast(totalSeconds, minutes)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(RewiBWPlugin.PREFIX + "Das Spiel endet in §f" + (
                        totalSeconds > 60
                                ? minutes + "§7 Minuten."
                                : totalSeconds + "§7 Sekunden."
                ));
            }
        }

        if (totalSeconds > 0) return;

        GameStateManager.setActiveGameState(EndGameState.getInstance());
    }

    private static boolean isTimeValidForBroadcast(int totalSeconds, int minutes) {
        // Every 10 minutes
        if (minutes % 10 == 0 && totalSeconds % 60 == 0) return true;
        // Every minute for last 5 minutes
        if (minutes <= 5 && totalSeconds % 60 == 0) return true;

        // On 30 seconds mark and last 10 seconds
        return totalSeconds == 30 || totalSeconds <= 10;
    }
}
