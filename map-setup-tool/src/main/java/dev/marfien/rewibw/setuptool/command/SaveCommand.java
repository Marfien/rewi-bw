package dev.marfien.rewibw.setuptool.command;

import dev.marfien.rewibw.setuptool.SetupSession;
import dev.marfien.rewibw.setuptool.SetupToolPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SaveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        SetupSession session = SetupToolPlugin.getSession(player);

        if (session == null) {
            player.sendMessage("§cYou are not in a setup session.");
            return true;
        }

        Path savePath = args.length == 0
                ? Bukkit.getWorldContainer().toPath().resolve(session.getWorld().getName()).resolve("config.yaml")
                : Paths.get(String.join(" ", args));

        player.sendMessage("§7Saving map config...");
        session.save(savePath);
        player.sendMessage("§aMap config saved.");

        return false;
    }
}
