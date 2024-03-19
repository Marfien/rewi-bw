package dev.marfien.rewibw.setuptool;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import dev.marfien.rewibw.setuptool.command.SetupCommand;
import lombok.Getter;
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

    public static SetupSession getSession(Player player) {
        return sessions.get(player);
    }

    public static void setSession(Player player, SetupSession session) {
        sessions.put(player, session);
    }

    @Override
    public void onEnable() {
        super.getCommand("setup").setExecutor(new SetupCommand());
        effectManager = new EffectManager(this);
    }
}
