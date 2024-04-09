package dev.marfien.rewibw.scoreboard;

import dev.marfien.rewibw.RewiBWPlugin;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@UtilityClass
public class CustomScoreboardManager {

    private static final Map<String, ScoreboardTeam> teams = new HashMap<>();
    private static final Map<String, ScoreboardObjective> objectives = new HashMap<>();

    public static void init() {
        Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), RewiBWPlugin.getInstance());
    }

    public static ScoreboardTeam registerTeam(String name) {
        Validate.notNull(name, "Team name cannot be null");
        Validate.isTrue(name.length() <= 16, "Team name '" + name + "' is longer than the limit of 16 characters");
        Validate.isTrue(!hasTeam(name), "Team name '" + name + "' is already in use");

        ScoreboardTeam team = new ScoreboardTeam(name);

        teams.put(name, team);
        for (Player player : Bukkit.getOnlinePlayers()) {
            Team bukkitTeam = player.getScoreboard().registerNewTeam(name);
            team.apply(player, bukkitTeam);
        }

        return team;
    }

    public static void unregisterTeam(String name) {
        ScoreboardTeam team = teams.remove(name);
        if (team == null) return;

        team.unregistered();
    }

    public static boolean hasTeam(String name) {
        return teams.containsKey(name);
    }

    static void updateTeam(String name, BiConsumer<Player, Team> updater) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Team team = player.getScoreboard().getTeam(name);
            updater.accept(player, team);
        }
    }

    public static ScoreboardObjective registerObjective(String name, String criteria) {
        Validate.isTrue(!hasObjective(name), "Objective with name '" + name + "' already registered");
        ScoreboardObjective objective = new ScoreboardObjective(name, criteria);

        objectives.put(name, objective);
        for (Player player : Bukkit.getOnlinePlayers()) {
            Objective bukkitObjective = player.getScoreboard().registerNewObjective(name, criteria);
            objective.apply(bukkitObjective);
        }

        return objective;
    }

    public static void unregisterObjective(String name) {
        ScoreboardObjective objective = objectives.remove(name);
        if (objective == null) return;

        objective.unregistered();
    }

    public static boolean hasObjective(String name) {
        return objectives.containsKey(name);
    }

    static void updateObjective(String name, Consumer<Objective> updater) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Objective objective = player.getScoreboard().getObjective(name);
            updater.accept(objective);
        }
    }

    private static class ScoreboardListener implements Listener {

        @EventHandler
        private void onJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            player.setScoreboard(getNewScoreboard(player));
        }

        private static Scoreboard getNewScoreboard(Player player) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            for (Map.Entry<String, ScoreboardTeam> teamEntry : teams.entrySet()) {
                Team team = scoreboard.registerNewTeam(teamEntry.getKey());
                teamEntry.getValue().apply(player, team);
            }

            for (Map.Entry<String, ScoreboardObjective> entry : objectives.entrySet()) {
                String name = entry.getKey();
                ScoreboardObjective objective = entry.getValue();
                Objective bukkitObjective = scoreboard.registerNewObjective(name, objective.getCriteria());
                objective.apply(bukkitObjective);
            }

            return scoreboard;
        }

    }

}
