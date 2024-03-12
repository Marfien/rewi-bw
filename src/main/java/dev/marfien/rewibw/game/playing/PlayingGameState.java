package dev.marfien.rewibw.game.playing;

import dev.marfien.rewibw.PlayerManager;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.GameState;
import dev.marfien.rewibw.game.playing.item.MobileShop;
import dev.marfien.rewibw.game.playing.item.Parachute;
import dev.marfien.rewibw.game.playing.item.RescuePlatform;
import dev.marfien.rewibw.game.playing.item.Teleporter;
import dev.marfien.rewibw.game.playing.listener.*;
import dev.marfien.rewibw.scoreboard.CustomScoreboardManager;
import dev.marfien.rewibw.scoreboard.ScoreboardObjective;
import dev.marfien.rewibw.scoreboard.ScoreboardTeam;
import dev.marfien.rewibw.team.GameTeam;
import dev.marfien.rewibw.team.TeamManager;
import dev.marfien.rewibw.usable.ConsumeType;
import dev.marfien.rewibw.usable.UsableItemInfo;
import dev.marfien.rewibw.usable.UsableItemManager;
import dev.marfien.rewibw.util.Items;
import dev.marfien.rewibw.util.StatsTracker;
import dev.marfien.rewibw.util.Strings;
import dev.marfien.rewibw.voting.MapVoting;
import dev.marfien.rewibw.world.GameMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.ArrayList;
import java.util.Collection;

public class PlayingGameState extends GameState {

    @Getter
    private static final PlayingGameState instance = new PlayingGameState();

    @Getter
    @Setter
    private static GameMap map;

    @Getter
    private static ScoreboardObjective sidebarObjective;
    @Getter
    private static ScoreboardTeam killsTeam;

    private final IngameCountdown countdown = new IngameCountdown();
    private final UsableItemManager itemManager = new UsableItemManager();
    private final Collection<BukkitTask> spawnerTasks = new ArrayList<>();

    private PlayingGameState() {
        this.itemManager.putHandler(Items.SPECTATOR_COMPASS, new UsableItemInfo(ConsumeType.NONE, event -> {
            event.getPlayer().sendMessage("Spec Inv öffnen");
        }));
        this.itemManager.putHandler(Items.RESCUE_PLATFORM, new RescuePlatform());
        this.itemManager.putHandler(Items.MOBILE_SHOP, new MobileShop());
        this.itemManager.putHandler(Items.PARACHUTE, new Parachute());
        this.itemManager.putHandler(Items.TELEPORTER, new Teleporter());
    }

    private final Listener[] listeners = new Listener[]{
            new MapProtectionListener(),
            new WebListener(),
            new PlayerDeathListener(),
            new MiscListener(),
            new TeamSpawnProtectListener(),
            new SpectatorListener(),
            new BlockListener(),
            new Parachute.ParachuteListener(),
            new Teleporter.TeleporterListener(),
    };

    @Override
    public Listener[] getListeners() {
        return this.listeners;
    }

    @Override
    public void onStart() {
        map.load();
        TeamManager.assignTeams();
        TeamManager.broadcastTeams();
        for (GameTeam team : TeamManager.getTeams()) {
            Location spawn = team.getSpawn();
            for (Player member : team.getMembers()) {
                member.teleport(spawn);
                PlayerManager.resetPlayerStatus(member);
            }
        }

        map.getSpawnerLocations().forEach((type, locations) -> {
            this.spawnerTasks.add(type.startSpawning(locations));
        });

        this.itemManager.register();

        int playersPerTeam = RewiBWPlugin.getPlayersPerTeam();
        sidebarObjective = CustomScoreboardManager.registerObjective("sidebar", "dummy");
        sidebarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        sidebarObjective.setDisplayName("§3BedWars §7- §b60:00");
        sidebarObjective.setScore("§6marfien.dev", playersPerTeam + 6);
        sidebarObjective.setScore("§7GameID: §r" + Strings.generateGameId(), playersPerTeam + 5);
        sidebarObjective.setScore("§0", playersPerTeam + 4);
        sidebarObjective.setScore("§6Kills", playersPerTeam + 3);
        killsTeam = CustomScoreboardManager.registerTeam("stat_kills");
        killsTeam.addEntry("§1");
        killsTeam.setPrefix(player -> "§r" + StatsTracker.getKills(player.getUniqueId()));
        sidebarObjective.setScore("§1", playersPerTeam + 2);
        sidebarObjective.setScore("§2", playersPerTeam + 1);
        for (GameTeam team : TeamManager.getTeams()) {
            String displayName = team.getColor().getDisplayName();
            sidebarObjective.setScore(displayName, team.size());
        }

        this.countdown.start();
    }

    @Override
    public void onStop() {
        this.countdown.stop();
        this.itemManager.shutdown();
        this.spawnerTasks.forEach(BukkitTask::cancel);
    }
}
