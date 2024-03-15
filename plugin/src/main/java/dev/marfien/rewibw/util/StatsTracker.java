package dev.marfien.rewibw.util;

import dev.marfien.rewibw.game.playing.PlayingGameState;
import dev.marfien.rewibw.scoreboard.ScoreboardTeam;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatsTracker {

    private static final Map<UUID, Integer> kills = new HashMap<>();

    public static void addKill(UUID player) {
        kills.put(player, kills.getOrDefault(player, 0) + 1);

        ScoreboardTeam killsTeam = PlayingGameState.getKillsTeam();
        if (killsTeam != null) {
            killsTeam.updatePrefix();
        }
    }

    public static int getKills(UUID player) {
        return kills.getOrDefault(player, 0);
    }

}
