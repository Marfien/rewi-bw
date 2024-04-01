package dev.marfien.rewibw;

import de.slikey.effectlib.EffectManager;
import dev.marfien.rewibw.command.ForceMapCommand;
import dev.marfien.rewibw.command.StartCommand;
import dev.marfien.rewibw.fakeentities.FakeEntityManager;
import dev.marfien.rewibw.game.GameStateManager;
import dev.marfien.rewibw.game.lobby.LobbyGameState;
import dev.marfien.rewibw.listener.PlayerConnectionListener;
import dev.marfien.rewibw.listener.WorldListener;
import dev.marfien.rewibw.scoreboard.CustomScoreboardManager;
import dev.marfien.rewibw.shared.TeamColor;
import dev.marfien.rewibw.shared.config.PluginConfig;
import dev.marfien.rewibw.shared.gui.GuiInventory;
import dev.marfien.rewibw.shared.usable.ConsumeType;
import dev.marfien.rewibw.shared.usable.UsableItemInfo;
import dev.marfien.rewibw.shared.usable.UsableItemManager;
import dev.marfien.rewibw.util.Items;
import dev.marfien.rewibw.voting.MapVoting;
import dev.marfien.rewibw.world.MapPool;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class RewiBWPlugin extends JavaPlugin {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final String PREFIX = "§8[§3BedWars§8] §r";

    private static RewiBWPlugin instance;
    public static final Vector ZERO_VECTOR = new Vector(0, 0, 0);

    @Getter
    private static EffectManager effectManager;
    @Getter
    private static PluginConfig config;

    public static RewiBWPlugin getInstance() {
        if (instance == null)
            throw new IllegalStateException("The Plugin is not initialized yet.");
        return instance;
    }

    private final UsableItemManager globalItemManager = new UsableItemManager();

    @Override
    public void onLoad() {
        super.saveResource("config.yaml", false);
        try {
            config = PluginConfig.loader(this).load().get(PluginConfig.class);
            LOGGER.info("Successfully loaded plugin configuration");
        } catch (ConfigurateException e) {
            LOGGER.error("Could not load configuration file", e);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        effectManager = new EffectManager(this);
        instance = this;

        this.globalItemManager.putHandler(Items.QUIT_ITEM, new UsableItemInfo(ConsumeType.NONE, event -> event.getPlayer().kickPlayer("§cDu hast den Server verlassen!")));
    }

    @Override
    @SneakyThrows(IOException.class)
    public void onEnable() {
        MapPool.loadMaps(config.getMapPool());

        Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        GameStateManager.setActiveGameState(new LobbyGameState(config.getLobbyMap()));
        FakeEntityManager.init(this);
        GuiInventory.setPlugin(this);
        CustomScoreboardManager.init();
        MapVoting.init(config.getVoting());
        this.globalItemManager.register(this);

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
