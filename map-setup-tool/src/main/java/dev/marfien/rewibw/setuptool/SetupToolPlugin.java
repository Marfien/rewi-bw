package dev.marfien.rewibw.setuptool;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import dev.marfien.rewibw.setuptool.command.SaveCommand;
import dev.marfien.rewibw.setuptool.command.SetupCommand;
import dev.marfien.rewibw.setuptool.item.*;
import dev.marfien.rewibw.setuptool.listener.WorldNoOpListener;
import dev.marfien.rewibw.shared.TeamColor;
import dev.marfien.rewibw.shared.gui.GuiInventory;
import dev.marfien.rewibw.shared.usable.UsableItemManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SetupToolPlugin extends JavaPlugin {

    public static Path IMPORT_PATH;

    @Getter
    private static EffectManager effectManager;

    private static final Map<Player, SetupSession> sessions = new HashMap<>();
    public static final Collection<Effect> effects = new ArrayList<>();
    private static final UsableItemManager itemManager = new UsableItemManager();

    public SetupToolPlugin() {
        GuiInventory.setPlugin(this);
    }

    public static SetupSession getSession(Player player) {
        return sessions.get(player);
    }

    public static void setSession(Player player, SetupSession session) {
        sessions.put(player, session);
    }

    @Override
    public void onLoad() {
        super.saveDefaultConfig();
        FileConfiguration configuration = super.getConfig();
        IMPORT_PATH = Paths.get(configuration.getString("import-path"));
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
        effectManager = new EffectManager(this);
        itemManager.register(this);

        Bukkit.getPluginManager().registerEvents(new WorldNoOpListener(), this);
    }
}
