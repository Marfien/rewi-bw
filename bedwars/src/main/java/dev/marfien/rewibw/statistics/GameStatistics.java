package dev.marfien.rewibw.statistics;

import lombok.Getter;

@Getter
public class GameStatistics implements GameStatisticsSnapshot {

    private int kills;

    public void addKill() {
        this.kills++;
    }

}
