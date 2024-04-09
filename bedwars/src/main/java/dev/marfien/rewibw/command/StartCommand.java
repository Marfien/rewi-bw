package dev.marfien.rewibw.command;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.Countdown;
import dev.marfien.rewibw.game.GameState;
import dev.marfien.rewibw.game.GameStateManager;
import dev.marfien.rewibw.game.lobby.LobbyGameState;
import dev.marfien.rewibw.shared.CustomCommand;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class StartCommand implements CustomCommand {

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        GameState currentGameState = GameStateManager.getActiveGameState();
        if (!(currentGameState instanceof LobbyGameState)) {
            commandSender.sendMessage(RewiBWPlugin.PREFIX + Message.GAME_ALREADY_RUNNING);
            return;
        }

        Countdown countdown = currentGameState.getCountdown();

        if (!countdown.isRunning()) {
            commandSender.sendMessage(RewiBWPlugin.PREFIX + Message.COUNTDOWN_STARTED);
            countdown.start();
            return;
        }

        if (countdown.getSeconds() <= 10) {
            commandSender.sendMessage(RewiBWPlugin.PREFIX + Message.START_COMMAND_TOO_LATE);
            return;
        }

        commandSender.sendMessage(RewiBWPlugin.PREFIX + Message.START_COMMAND_COUNTDOWN_REDUCED);
        countdown.setSeconds(10);
    }
}
