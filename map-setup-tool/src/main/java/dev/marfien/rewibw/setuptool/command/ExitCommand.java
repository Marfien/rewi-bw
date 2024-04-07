package dev.marfien.rewibw.setuptool.command;

import dev.marfien.rewibw.setuptool.SetupSession;
import dev.marfien.rewibw.setuptool.SetupSessionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

public class ExitCommand implements CommandExecutor {

    private static final Collection<Player> waitingForConfirmation = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) commandSender;
        if (!waitingForConfirmation.contains(player)) {
            waitingForConfirmation.add(player);
            player.sendMessage("§7Are you sure you want to cancel the setup session? Type §a/exit §7again to confirm. Remember to save it!");
            return false;
        }

        Optional<SetupSession> optionalSetupSession = SetupSessionManager.getSession(player.getUniqueId());

        if (!optionalSetupSession.isPresent()) {
            player.sendMessage("§cYou are not in a setup session.");
            return false;
        }

        SetupSession setupSession = optionalSetupSession.get();
        SetupSessionManager.removeSession(player.getUniqueId());
        waitingForConfirmation.remove(player);
        player.sendMessage("§cSetup session exited.");
        player.teleport(Bukkit.getWorld("world").getSpawnLocation());
        Bukkit.unloadWorld(setupSession.getWorld(), false);
        return false;
    }
}
