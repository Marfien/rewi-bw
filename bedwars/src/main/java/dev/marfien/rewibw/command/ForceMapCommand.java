package dev.marfien.rewibw.command;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.Countdown;
import dev.marfien.rewibw.game.GameState;
import dev.marfien.rewibw.game.GameStateManager;
import dev.marfien.rewibw.game.lobby.LobbyGameState;
import dev.marfien.rewibw.game.lobby.MapVoting;
import dev.marfien.rewibw.shared.CustomCommand;
import dev.marfien.rewibw.world.MapPool;
import dev.marfien.rewibw.world.MapWorld;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ForceMapCommand implements CustomCommand, TabExecutor {

    private final MapVoting mapVoting;

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        GameState currentGameState = GameStateManager.getActiveGameState();
        if (!(currentGameState instanceof LobbyGameState)) {
            commandSender.sendMessage(RewiBWPlugin.PREFIX + Message.GAME_ALREADY_RUNNING);
            return;
        }

        Countdown countdown = currentGameState.getCountdown();

        if (countdown.isRunning() && countdown.getSeconds() <= 10) {
            commandSender.sendMessage(RewiBWPlugin.PREFIX + Message.FORCEMAP_COMMAND_TOO_LATE);
            return;
        }

        if (args.length != 1) {
            commandSender.sendMessage(RewiBWPlugin.PREFIX + Message.FORCEMAP_COMMAND_USAGE);
            return;
        }

        String mapName = args[0];

        if (!MapPool.contains(mapName)) {
            commandSender.sendMessage(RewiBWPlugin.PREFIX + Message.UNKNOWN_MAP.format(mapName));
            commandSender.sendMessage(RewiBWPlugin.PREFIX + Message.AVAILABLE_MAPS.format(String.join(", ", MapPool.getMapNames())));
            return;
        }

        try {
            MapWorld mapWorld = MapPool.requestMap(mapName);
            this.mapVoting.setWinner(mapWorld);
            commandSender.sendMessage(RewiBWPlugin.PREFIX + Message.MAP_CHANGED.format(mapWorld.getConfig().getMap().getDisplayName()));
        } catch (IOException e) {
            commandSender.sendMessage(RewiBWPlugin.PREFIX + "§cAn error occurred while loading the map: §4" + e.getMessage());
            RewiBWPlugin.getPluginLogger().error("Error while loading map: {}", mapName, e);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length != 1) return Collections.emptyList();

        return MapPool.getMapNames()
                .stream()
                .filter(name -> StringUtils.startsWithIgnoreCase(name, args[0]))
                .collect(Collectors.toList());
    }
}
