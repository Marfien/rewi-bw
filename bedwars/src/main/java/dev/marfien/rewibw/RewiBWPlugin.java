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
import dev.marfien.rewibw.shared.config.PluginConfig;
import dev.marfien.rewibw.shared.gui.GuiInventory;
import dev.marfien.rewibw.shared.usable.ConsumeType;
import dev.marfien.rewibw.shared.usable.UsableItemInfo;
import dev.marfien.rewibw.shared.usable.UsableItemManager;
import dev.marfien.rewibw.team.TeamManager;
import dev.marfien.rewibw.util.Items;
import dev.marfien.rewibw.voting.MapVoting;
import dev.marfien.rewibw.world.MapPool;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.spongepowered.configurate.ConfigurateException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class RewiBWPlugin extends JavaPlugin {

    public static final String PREFIX = "§8[§3BedWars§8] §r";

    private static RewiBWPlugin instance;
    { instance = this; }
    public static final Vector ZERO_VECTOR = new Vector(0, 0, 0);

    @Getter
    private static EffectManager effectManager;
    @Getter
    private static PluginConfig pluginConfig;

    public static Logger getPluginLogger() {
        return instance.getLogger();
    }

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
            pluginConfig = PluginConfig.loader(this).load().require(PluginConfig.class);
            super.getLogger().info("Successfully loaded plugin configuration");
        } catch (ConfigurateException e) {
            super.getLogger().log(Level.SEVERE, "Could not load configuration file", e);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        effectManager = new EffectManager(this);

        this.globalItemManager.putHandler(Items.QUIT_ITEM, new UsableItemInfo(ConsumeType.NONE, event -> event.getPlayer().kickPlayer("§cDu hast den Server verlassen!")));
    }

    @Override
    @SneakyThrows(IOException.class)
    public void onEnable() {
        MapPool.loadMaps(pluginConfig.getMapPool());

        Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        GameStateManager.setActiveGameState(new LobbyGameState(pluginConfig.getLobbyMap()));
        FakeEntityManager.init(this);
        GuiInventory.setPlugin(this);
        CustomScoreboardManager.init();
        MapVoting.init(pluginConfig.getVoting());
        TeamManager.init();
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
