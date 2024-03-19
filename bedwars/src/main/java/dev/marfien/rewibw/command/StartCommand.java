package dev.marfien.rewibw.command;

import dev.marfien.rewibw.Message;
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
            commandSender.sendMessage(RewiBWPlugin.PREFIX + Message.GAME_ALREADY_RUNNING);
            return true;
        }

        LobbyCountdown countdown = LobbyGameState.getInstance().getCountdown();

        if (!countdown.isRunning()) {
            commandSender.sendMessage(RewiBWPlugin.PREFIX + Message.COUNTDOWN_STARTED);
            LobbyGameState.getInstance().getCountdown().start();
            return true;
        }

        if (countdown.getSeconds() <= 10) {
            commandSender.sendMessage(RewiBWPlugin.PREFIX + Message.START_COMMAND_TOO_LATE);
            return true;
        }

        commandSender.sendMessage(RewiBWPlugin.PREFIX + Message.START_COMMAND_COUNTDOWN_REDUCED);
        countdown.setSeconds(10);
        return true;
    }
}
