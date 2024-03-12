package dev.marfien.rewibw.commands;

import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.GameStateManager;
import dev.marfien.rewibw.game.lobby.LobbyCountdown;
import dev.marfien.rewibw.game.lobby.LobbyGameState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public class ForceMapCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (GameStateManager.getActiveGameState() != LobbyGameState.getInstance()) {
            commandSender.sendMessage(RewiBWPlugin.PREFIX + "§cDas Spiel läuft bereits.");
            return true;
        }

        LobbyCountdown countdown = LobbyGameState.getInstance().getCountdown();

        if (countdown.getSeconds() <= 10) {
            commandSender.sendMessage(RewiBWPlugin.PREFIX + "§cDu kannst in den letzten 10 Sekunden die Map nicht mehr ändern.");
            return true;
        }

        commandSender.sendMessage(RewiBWPlugin.PREFIX + "Der Countdown wurde verkürzt.");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
