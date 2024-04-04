package dev.marfien.rewibw.command;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.GameState;
import dev.marfien.rewibw.game.GameStateManager;
import dev.marfien.rewibw.game.playing.PlayingGameState;
import dev.marfien.rewibw.world.MapWorld;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MapCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        GameState currentGameState = GameStateManager.getActiveGameState();
        if (!(currentGameState instanceof PlayingGameState)) {
            commandSender.sendMessage(RewiBWPlugin.PREFIX + Message.GAME_NOT_STARTED_YET);
            return true;
        }

        MapWorld mapWorld = ((PlayingGameState) currentGameState).getMap();
        commandSender.sendMessage(RewiBWPlugin.PREFIX + Message.MAP_NAME.format(mapWorld.getConfig().getMap().getDisplayName(), mapWorld.getName()));
        return true;
    }



}
