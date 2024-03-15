package dev.marfien.rewibw.commands;

import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.GameStateManager;
import dev.marfien.rewibw.game.lobby.LobbyCountdown;
import dev.marfien.rewibw.game.lobby.LobbyGameState;
import dev.marfien.rewibw.voting.MapVoting;
import dev.marfien.rewibw.world.GameMapInfo;
import dev.marfien.rewibw.world.MapPool;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ForceMapCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (GameStateManager.getActiveGameState() != LobbyGameState.getInstance()) {
            commandSender.sendMessage(RewiBWPlugin.PREFIX + "§cDas Spiel läuft bereits.");
            return true;
        }

        LobbyCountdown countdown = LobbyGameState.getInstance().getCountdown();

        if (countdown.getSeconds() <= 10) {
            commandSender.sendMessage(RewiBWPlugin.PREFIX + "§cDu kannst in den letzten 10 Sekunden die Map nicht mehr ändern.");
            return true;
        }

        if (args.length != 1) {
            commandSender.sendMessage(RewiBWPlugin.PREFIX + "§cVerwendung: /forcemap <name>");
            return true;
        }

        String mapName = args[0];
        GameMapInfo mapInfo = MapPool.getMapInfo(mapName);

        if (mapInfo == null) {
            commandSender.sendMessage(RewiBWPlugin.PREFIX + "§cDie Map §f" + mapName + "§c existiert nicht.");
            commandSender.sendMessage(RewiBWPlugin.PREFIX + "§7Verfügbare Maps: §f" + MapPool.getMaps().stream().map(GameMapInfo::getName).reduce((s1, s2) -> s1 + ", " + s2).orElse("§c-"));
            return true;
        }

        MapVoting.setWinner(mapInfo);
        commandSender.sendMessage(RewiBWPlugin.PREFIX + "§aDie Map wurde auf §f" + mapInfo.getDisplayName() + "§a geändert.");
        return true;
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
