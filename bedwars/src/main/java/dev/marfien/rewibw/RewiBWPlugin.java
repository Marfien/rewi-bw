package dev.marfien.rewibw;

import de.slikey.effectlib.EffectManager;
import dev.marfien.rewibw.command.ForceMapCommand;
import dev.marfien.rewibw.command.MapCommand;
import dev.marfien.rewibw.command.MusicCommand;
import dev.marfien.rewibw.command.StartCommand;
import dev.marfien.rewibw.fakeentities.FakeEntityManager;
import dev.marfien.rewibw.game.GameStateManager;
import dev.marfien.rewibw.game.lobby.LobbyGameState;
import dev.marfien.rewibw.listener.PlayerConnectionListener;
import dev.marfien.rewibw.listener.WorldListener;
import dev.marfien.rewibw.scoreboard.CustomScoreboardManager;
import dev.marfien.rewibw.shared.Scheduler;
import dev.marfien.rewibw.shared.config.ConfigLoader;
import dev.marfien.rewibw.shared.gui.GuiInventory;
import dev.marfien.rewibw.shared.logging.PrefixedLoggerFactory;
import dev.marfien.rewibw.shared.usable.ConsumeType;
import dev.marfien.rewibw.shared.usable.UsableItemInfo;
import dev.marfien.rewibw.shared.usable.UsableItemManager;
import dev.marfien.rewibw.team.TeamManager;
import dev.marfien.rewibw.util.Items;
import dev.marfien.rewibw.world.MapPool;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.IOException;

@Getter
public class RewiBWPlugin extends JavaPlugin {

    public static final String PREFIX = "§8[§3BedWars§8] §r";

    private static RewiBWPlugin instance;
    public static final Vector ZERO_VECTOR = new Vector(0, 0, 0);

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private static EffectManager effectManager;
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private static RewiBWConfig pluginConfig;
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private static Scheduler scheduler;
    @Getter
    private static final Logger pluginLogger = PrefixedLoggerFactory.getLogger("[rewi-bw]");

    public static RewiBWPlugin getInstance() {
        if (instance == null)
            throw new IllegalStateException("The Plugin is not initialized yet.");
        return instance;
    }

    public static void setInstance(RewiBWPlugin instance) {
        if (RewiBWPlugin.instance != null)
            throw new IllegalStateException("The Plugin is already initialized.");
        RewiBWPlugin.instance = instance;
    }

    private final UsableItemManager globalItemManager = new UsableItemManager();

    public RewiBWPlugin() {
        setInstance(this);
        setEffectManager(new EffectManager(this));
        setScheduler(new Scheduler(this));
    }

    @Override
    public void onLoad() {
        try {
            ConfigurationLoader<?> loader = ConfigLoader.loadPluginConfig(this);
            ConfigurationNode node = loader.load();
            if (node.empty()) {
                pluginLogger.warn("Configuration file is empty, using default values");
                node.set(new RewiBWConfig());
            }
            setPluginConfig(node.get(RewiBWConfig.class));
            pluginLogger.info("Successfully loaded plugin configuration");
        } catch (ConfigurateException e) {
            pluginLogger.error("Could not load configuration file: {}", e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.globalItemManager.putHandler(Items.QUIT_ITEM, new UsableItemInfo(ConsumeType.NONE, event -> event.getPlayer().kickPlayer("§cDu hast den Server verlassen!")));
    }

    @Override
    @SneakyThrows(IOException.class)
    public void onEnable() {
        FakeEntityManager.init(this);
        GuiInventory.setPlugin(this);

        MapPool.loadMaps(pluginConfig.getMapPool());
        LobbyGameState lobbyGameState = new LobbyGameState(pluginConfig.getLobbyMap(), pluginConfig.getVoting());

        Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        GameStateManager.setActiveGameState(lobbyGameState);
        CustomScoreboardManager.init();
        TeamManager.init();
        this.globalItemManager.register(this);

        Bukkit.getPluginCommand("start").setExecutor(new StartCommand());
        Bukkit.getPluginCommand("map").setExecutor(new MapCommand());
        Bukkit.getPluginCommand("forcemap").setExecutor(new ForceMapCommand(lobbyGameState.getMapVoting()));
        Bukkit.getPluginCommand("music").setExecutor(new MusicCommand());

        // g#
        //
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
