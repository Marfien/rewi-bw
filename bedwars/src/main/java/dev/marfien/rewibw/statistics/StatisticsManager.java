package dev.marfien.rewibw.statistics;

import dev.marfien.rewibw.game.playing.PlayingGameState;
import dev.marfien.rewibw.scoreboard.ScoreboardTeam;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatisticsManager {

    private static final Map<UUID, GameStatistics> stats = new HashMap<>();

    public static void addKill(UUID player) {
        stats.computeIfAbsent(player, p -> new GameStatistics()).addKill();

        ScoreboardTeam killsTeam = PlayingGameState.getKillsTeam();
        if (killsTeam != null) {
            killsTeam.updatePrefix();
        }
    }

    public static int getKills(UUID player) {
        return stats.computeIfAbsent(player, p -> new GameStatistics()).getKills();
    }

    public static GameStatisticsSnapshot getSnapshot(UUID player) {
        return stats.computeIfAbsent(player, p -> new GameStatistics());
    }

}
