package dev.marfien.rewibw.setuptool.command;

import dev.marfien.rewibw.setuptool.SetupSession;
import dev.marfien.rewibw.setuptool.SetupSessionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ToWorldCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Optional<SetupSession> setupSession = SetupSessionManager.getSession(((Player) commandSender).getUniqueId());

        if (!setupSession.isPresent()) {
            commandSender.sendMessage("§cYou are not in a setup session.");
            return true;
        }

        Player player = (Player) commandSender;
        player.teleport(setupSession.get().getWorld().getSpawnLocation());
        player.sendMessage("§aTeleported to setup session world.");
        return true;
    }

}
