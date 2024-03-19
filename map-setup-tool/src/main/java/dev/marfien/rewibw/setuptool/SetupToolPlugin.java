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
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SetupToolPlugin extends JavaPlugin {

    @Getter
    private static EffectManager effectManager;

    private static final Map<Player, SetupSession> sessions = new HashMap<>();
    public static final Collection<Effect> effects = new ArrayList<>();
    private static final UsableItemManager itemManager = new UsableItemManager();

    public SetupToolPlugin() {
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
    }

    public static SetupSession getSession(Player player) {
        return sessions.get(player);
    }

    public static void setSession(Player player, SetupSession session) {
        sessions.put(player, session);
    }

    @Override
    public void onEnable() {
        super.getCommand("setup").setExecutor(new SetupCommand());
        super.getCommand("save").setExecutor(new SaveCommand());
        effectManager = new EffectManager(this);
        itemManager.register(this);
        GuiInventory.setPlugin(this);

        Bukkit.getPluginManager().registerEvents(new WorldNoOpListener(), this);
    }
}
