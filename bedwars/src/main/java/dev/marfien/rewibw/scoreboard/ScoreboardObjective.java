package dev.marfien.rewibw.scoreboard;

import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class ScoreboardObjective {

    private final String name;
    private final String criteria;
    private boolean registered = true;

    private DisplaySlot displaySlot;
    private String displayName;

    private final Map<String, Integer> scores = new HashMap<>();

    public ScoreboardObjective(String name, String criteria) {
        this.name = name;
        this.displayName = name;
        this.criteria = criteria;
    }

    public void setDisplaySlot(DisplaySlot displaySlot) {
        this.displaySlot = displaySlot;
        tryUpdate(objective -> objective.setDisplaySlot(displaySlot));
    }

    public void setDisplayName(String displayName) {
        Validate.notNull(displayName, "Display name cannot be null");
        Validate.isTrue(displayName.length() <= 32, "Display name '" + displayName + "' is longer than the limit of 32 characters");
        this.displayName = displayName;
        tryUpdate(objective -> objective.setDisplayName(displayName));
    }

    public void setScore(String entry, int score) {
        Validate.notNull(entry, "Entry cannot be null");
        Validate.isTrue(entry.length() <= 40, "Entry cannot be longer than 40 characters!");
        this.scores.put(entry, score);
        tryUpdate(objective -> objective.getScore(entry).setScore(score));
    }

    public void removeScore(String entry) {
        this.scores.remove(entry);
        tryUpdate(objective -> objective.getScoreboard().resetScores(entry));
    }

    public void unregister() {
        CustomScoreboardManager.unregisterObjective(this.name);
    }

    void unregistered() {
        this.registered = false;
    }

    private void tryUpdate(Consumer<Objective> objectiveConsumer) {
        if (this.registered) {
            CustomScoreboardManager.updateObjective(this.name, objectiveConsumer);
        }
    }

    void apply(Objective bukkitObjective) {
        bukkitObjective.setDisplayName(this.displayName);
        bukkitObjective.setDisplaySlot(this.displaySlot);
        for (Map.Entry<String, Integer> entry : this.scores.entrySet()) {
            bukkitObjective.getScore(entry.getKey()).setScore(entry.getValue());
        }
    }
}
