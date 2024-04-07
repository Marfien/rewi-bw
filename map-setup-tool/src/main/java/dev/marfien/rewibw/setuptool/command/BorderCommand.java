package dev.marfien.rewibw.setuptool.command;

import dev.marfien.rewibw.setuptool.SetupSession;
import dev.marfien.rewibw.setuptool.SetupSessionManager;
import dev.marfien.rewibw.shared.config.MapConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class BorderCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) commandSender;
        Optional<SetupSession> optionalSetupSession = SetupSessionManager.getSession(player.getUniqueId());

        if (!optionalSetupSession.isPresent()) {
            player.sendMessage("§cYou are not in a setup session.");
            return true;
        }

        if (args.length != 4) {
            player.sendMessage("Usage: /border <x1> <x2> <z1> <z2>");
            return true;
        }

        try {
            int x1 = Integer.parseInt(args[0]);
            int x2 = Integer.parseInt(args[1]);
            int z1 = Integer.parseInt(args[2]);
            int z2 = Integer.parseInt(args[3]);

            optionalSetupSession.get().getMapConfig().getMap().setBorder(new MapConfig.BorderConfig(x1, x2, z1, z2));
            player.sendMessage("§aBorder set.");
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid number format: " + e.getMessage());
        }

        return false;
    }
}
