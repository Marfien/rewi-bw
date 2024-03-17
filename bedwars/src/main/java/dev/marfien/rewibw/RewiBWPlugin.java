package dev.marfien.rewibw;

import de.slikey.effectlib.EffectManager;
import dev.marfien.rewibw.commands.ForceMapCommand;
import dev.marfien.rewibw.commands.StartCommand;
import dev.marfien.rewibw.fakemobs.FakeEntityManager;
import dev.marfien.rewibw.game.GameStateManager;
import dev.marfien.rewibw.game.lobby.LobbyGameState;
import dev.marfien.rewibw.listener.PlayerConnectionListener;
import dev.marfien.rewibw.listener.WorldListener;
import dev.marfien.rewibw.scoreboard.CustomScoreboardManager;
import dev.marfien.rewibw.team.TeamColor;
import dev.marfien.rewibw.usable.ConsumeType;
import dev.marfien.rewibw.usable.UsableItemInfo;
import dev.marfien.rewibw.usable.UsableItemManager;
import dev.marfien.rewibw.util.Items;
import dev.marfien.rewibw.voting.MapVoting;
import dev.marfien.rewibw.world.GameMap;
import dev.marfien.rewibw.world.MapPool;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class RewiBWPlugin extends JavaPlugin {

    public static final String PREFIX = "§8[§3BedWars§8] §r";
    private static RewiBWPlugin instance;
    public static final Vector ZERO_VECTOR = new Vector(0, 0, 0);

    @Getter
    private static Collection<TeamColor> teams;
    @Getter
    private static int playersPerTeam;

    @Getter
    private static EffectManager effectManager;

    public static int getMaxPlayers() {
        return teams.size() * playersPerTeam;
    }

    public static int getMinPlayers() {
        return getMaxPlayers() / 2;
    }

    public static RewiBWPlugin getInstance() {
        if (instance == null)
            throw new IllegalStateException("The Plugin is not initialized yet.");
        return instance;
    }

    private final UsableItemManager globalItemManager = new UsableItemManager();

    public RewiBWPlugin() {
        this.globalItemManager.putHandler(Items.QUIT_ITEM, new UsableItemInfo(ConsumeType.NONE, event -> event.getPlayer().kickPlayer("§cDu hast den Server verlassen!")));
    }

    @Override
    public void onLoad() {
        super.saveDefaultConfig();
        FileConfiguration config = super.getConfig();
        teams = config.getStringList("teams.colors")
                .stream()
                .map(String::toUpperCase)
                .map(TeamColor::valueOf)
                .collect(Collectors.toSet());
        playersPerTeam = config.getInt("teams.players");
        effectManager = new EffectManager(this);

        instance = this;
    }

    @Override
    @SneakyThrows(IOException.class)
    public void onEnable() {
        FileConfiguration config = super.getConfig();
        this.getLogger().info("Loading Maps...");
        MapPool.loadMaps(Paths.get(config.getString("maps.path")));

        Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        GameStateManager.setActiveGameState(new LobbyGameState(config.getString("maps.lobby")));
        FakeEntityManager.init();
        CustomScoreboardManager.init();
        MapVoting.init(config.getIntegerList("voting.votable-slots").stream().mapToInt(i -> i).toArray());
        this.globalItemManager.register();

        Bukkit.getPluginCommand("start").setExecutor(new StartCommand());
        Bukkit.getPluginCommand("forcemap").setExecutor(new ForceMapCommand());
    }

    @Override
    public void onDisable() {
        EffectManager.disposeAll();
        this.globalItemManager.shutdown();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("§cDer Server wird neu gestartet");
        }
    }
}
