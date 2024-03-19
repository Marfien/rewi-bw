package dev.marfien.rewibw.setuptool;

import dev.marfien.rewibw.setuptool.command.SetupCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class SetupToolPlugin extends JavaPlugin {

    private static final Map<Player, SetupSession> sessions = new HashMap<>();

    public static SetupSession getSession(Player player) {
        return sessions.get(player);
    }

    public static void setSession(Player player, SetupSession session) {
        sessions.put(player, session);
    }

    @Override
    public void onEnable() {
        super.getCommand("setup").setExecutor(new SetupCommand());
    }
}
