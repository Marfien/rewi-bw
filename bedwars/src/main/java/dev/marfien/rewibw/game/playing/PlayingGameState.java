package dev.marfien.rewibw.game.playing;

import dev.marfien.rewibw.PlayerManager;
import dev.marfien.rewibw.ResourceType;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.fakeentities.FakeEntityManager;
import dev.marfien.rewibw.game.GameState;
import dev.marfien.rewibw.game.playing.item.*;
import dev.marfien.rewibw.game.playing.listener.*;
import dev.marfien.rewibw.scoreboard.CustomScoreboardManager;
import dev.marfien.rewibw.scoreboard.ScoreboardObjective;
import dev.marfien.rewibw.scoreboard.ScoreboardTeam;
import dev.marfien.rewibw.shared.Position;
import dev.marfien.rewibw.shared.config.MapConfig;
import dev.marfien.rewibw.shared.usable.UsableItemManager;
import dev.marfien.rewibw.shop.FakeDealer;
import dev.marfien.rewibw.statistics.StatisticsManager;
import dev.marfien.rewibw.team.GameTeam;
import dev.marfien.rewibw.team.TeamManager;
import dev.marfien.rewibw.util.Items;
import dev.marfien.rewibw.util.Strings;
import dev.marfien.rewibw.world.MapWorld;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;

public class PlayingGameState extends GameState {

    @Getter
    private final MapWorld map;

    @Getter
    private static ScoreboardObjective sidebarObjective;
    @Getter
    private static ScoreboardTeam killsTeam;

    @Getter
    private final IngameCountdown countdown = new IngameCountdown();
    private final UsableItemManager itemManager = new UsableItemManager();

    private BukkitTask borderTask;

    private final Listener[] listeners;

    public PlayingGameState(MapWorld map) {
        this.map = map;
        this.itemManager.putHandler(Items.SPECTATOR_COMPASS, new SpectatorCompass());
        this.itemManager.putHandler(Items.RESCUE_PLATFORM, new RescuePlatform());
        this.itemManager.putHandler(Items.MOBILE_SHOP, new MobileShop());
        this.itemManager.putHandler(Items.PARACHUTE, new Parachute());
        this.itemManager.putHandler(Items.TELEPORTER, new Teleporter());

        this.listeners = new Listener[]{
                new MapProtectionListener(),
                new WebListener(),
                new PlayerDeathListener(),
                new MiscListener(),
                new TeamSpawnProtectListener(),
                new SpectatorListener(map.asLocation(MapConfig::getSpectatorSpawn)),
                new BlockListener(map.getConfig().getMap().getBorder().getSnapshot()),
                new Parachute.ParachuteListener(),
                new Teleporter.TeleporterListener(),
                new SpectatorCompass.SpectatorCompassListener()
        };
    }

    @Override
    public Listener[] getListeners() {
        return this.listeners;
    }

    @Override
    public void onStart() {
        this.map.load();
        this.borderTask = new BorderTask(this.map).runTaskTimerAsynchronously(RewiBWPlugin.getInstance(), 10, 7);
        // Teleport players to their spawn
        for (GameTeam team : TeamManager.getTeams()) {
            team.init(this.map);
            Location spawn = team.getSpawn();
            for (Player member : team.getMembers()) {
                member.teleport(spawn);
                PlayerManager.resetPlayerStatus(member);
            }
        }

        TeamManager.broadcastTeams();
        buildScoreboard();
        SpectatorCompass.refreshInventory();
        this.itemManager.register(RewiBWPlugin.getInstance());
        this.countdown.start();
    }

    @Override
    public void onStop() {
        this.countdown.stop();
        this.itemManager.shutdown();
        this.borderTask.cancel();
        this.map.unload();
    }

    private static void buildScoreboard() {
        int playersPerTeam = RewiBWPlugin.getPluginConfig().getTeams().getPlayersPerTeam();
        sidebarObjective = CustomScoreboardManager.registerObjective("sidebar", "dummy");
        sidebarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        sidebarObjective.setDisplayName("§3BedWars §7- §b60:00");
        sidebarObjective.setScore("§6marfien.dev", playersPerTeam + 6);
        sidebarObjective.setScore("§7GameID: §r" + Strings.generateGameId(), playersPerTeam + 5);
        sidebarObjective.setScore("§0", playersPerTeam + 4);
        sidebarObjective.setScore("§6Kills", playersPerTeam + 3);
        killsTeam = CustomScoreboardManager.registerTeam("stat_kills");
        killsTeam.addEntry("§1");
        killsTeam.setPrefix(player -> "§r" + StatisticsManager.getKills(player.getUniqueId()));
        sidebarObjective.setScore("§1", playersPerTeam + 2);
        sidebarObjective.setScore("§2", playersPerTeam + 1);
        for (GameTeam team : TeamManager.getTeams()) {
            String displayName = team.getColor().getDisplayName();
            sidebarObjective.setScore(displayName, team.size());
        }
    }

}
