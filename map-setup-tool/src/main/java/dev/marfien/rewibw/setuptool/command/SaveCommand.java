package dev.marfien.rewibw.setuptool.command;

import dev.marfien.rewibw.setuptool.SetupSession;
import dev.marfien.rewibw.setuptool.SetupSessionManager;
import dev.marfien.rewibw.setuptool.SetupToolPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class SaveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        Optional<SetupSession> optionalSession = SetupSessionManager.getSession(player.getUniqueId());

        if (!optionalSession.isPresent()) {
            player.sendMessage("§cYou are not in a setup session.");
            return true;
        }

        SetupSession session = optionalSession.get();

        Path savePath = args.length == 0
                ? Bukkit.getWorldContainer().toPath().resolve(session.getWorld().getName()).resolve("config.yaml")
                : Paths.get(String.join(" ", args));

        if (session.getMapConfig().getSpectatorSpawn() == null) {
            player.sendMessage("§cSpectator spawn not set.");
            return true;
        }

        if (session.getMapConfig().getMap().getBorder() == null) {
            player.sendMessage("§eBorder is not set. Continuing. Safe again after setting border to add it to the config.");
        }

        player.sendMessage("§7Saving map config...");
        try {
            session.save(savePath);
            player.sendMessage("§aMap config saved.");
        } catch (Exception e) {
            player.sendMessage("§cFailed to save map config: " + e.getMessage());
            return true;
        }

        return true;
    }
}
