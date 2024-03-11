package dev.marfien.rewibw.commands;

import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.GameStateManager;
import dev.marfien.rewibw.game.lobby.LobbyCountdown;
import dev.marfien.rewibw.game.lobby.LobbyGameState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (GameStateManager.getActiveGameState() != LobbyGameState.getInstance()) {
            commandSender.sendMessage(RewiBWPlugin.PREFIX + "§cDas Spiel läuft bereits.");
            return true;
        }

        LobbyCountdown countdown = LobbyGameState.getInstance().getCountdown();

        if (!countdown.isRunning()) {
            commandSender.sendMessage(RewiBWPlugin.PREFIX + "Der Countdown wurde gestartet.");
            LobbyGameState.getInstance().getCountdown().start();
            return true;
        }

        if (countdown.getSeconds() <= 10) {
            commandSender.sendMessage(RewiBWPlugin.PREFIX + "§cDer Countdown kann nicht mehr verkürzt werden.");
            return true;
        }

        commandSender.sendMessage(RewiBWPlugin.PREFIX + "Der Countdown wurde verkürzt.");
        countdown.setSeconds(10);
        return true;
    }
}
