package dev.marfien.rewibw.setuptool;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import dev.marfien.rewibw.setuptool.command.*;
import dev.marfien.rewibw.setuptool.item.*;
import dev.marfien.rewibw.setuptool.listener.WorldNoOpListener;
import dev.marfien.rewibw.shared.TeamColor;
import dev.marfien.rewibw.shared.config.ConfigLoader;
import dev.marfien.rewibw.shared.gui.GuiInventory;
import dev.marfien.rewibw.shared.logging.PrefixedLoggerFactory;
import dev.marfien.rewibw.shared.usable.UsableItemManager;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.util.ArrayList;
import java.util.Collection;

public class SetupToolPlugin extends JavaPlugin {

    @Getter
    private final Logger pluginLogger = PrefixedLoggerFactory.getLogger(this);

    @Getter
    private static SetupToolConfig pluginConfig;

    @Getter
    private static EffectManager effectManager;

    public static final Collection<Effect> effects = new ArrayList<>();
    private static final UsableItemManager itemManager = new UsableItemManager();

    public SetupToolPlugin() {
        GuiInventory.setPlugin(this);
    }

    @Override
    public void onLoad() {
        try {
            ConfigurationLoader<?> loader = ConfigLoader.loadPluginConfig(this);
            ConfigurationNode node = loader.load();
            if (node.empty()) {
                pluginLogger.warn("Configuration file is empty, using default values");
                node.set(new SetupToolConfig());
            }
            pluginConfig = node.get(SetupToolConfig.class);
            pluginLogger.info("Successfully loaded plugin configuration");
        } catch (ConfigurateException e) {
            pluginLogger.error("Could not load configuration file: {}", e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
    }

    @Override
    public void onEnable() {
        itemManager.putHandler(BronzeSpawnerAdder.ITEM, new BronzeSpawnerAdder());
        itemManager.putHandler(SilverSpawnerAdder.ITEM, new SilverSpawnerAdder());
        itemManager.putHandler(GoldSpawnerAdder.ITEM, new GoldSpawnerAdder());
        itemManager.putHandler(LocationRemover.ITEM, new LocationRemover());
        itemManager.putHandler(ShopAdder.ITEM, new ShopAdder());
        itemManager.putHandler(SpecSpawnSetter.ITEM, new SpecSpawnSetter());
        itemManager.putHandler(TeamAdder.ITEM, new TeamAdder());
        for (TeamColor value : TeamColor.values()) {
            itemManager.putHandler(TeamSpawnAdder.getItemFor(value), new TeamSpawnAdder(value));
            itemManager.putHandler(TeamBedSetter.getItemFor(value), new TeamBedSetter(value));
        }

        super.getCommand("setup").setExecutor(new SetupCommand());
        super.getCommand("save").setExecutor(new SaveCommand());
        super.getCommand("to-world").setExecutor(new ToWorldCommand());
        super.getCommand("exit").setExecutor(new ExitCommand());
        super.getCommand("get-items").setExecutor(new GetItemsCommand());
        effectManager = new EffectManager(this);
        itemManager.register(this);

        Bukkit.getPluginManager().registerEvents(new WorldNoOpListener(), this);
    }
}
