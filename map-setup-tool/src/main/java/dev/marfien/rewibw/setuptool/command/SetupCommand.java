package dev.marfien.rewibw.setuptool.command;

import dev.marfien.rewibw.setuptool.SetupSession;
import dev.marfien.rewibw.setuptool.SetupSessionManager;
import dev.marfien.rewibw.setuptool.SetupToolPlugin;
import dev.marfien.rewibw.setuptool.item.*;
import dev.marfien.rewibw.shared.FileUtils;
import dev.marfien.rewibw.shared.config.MapConfig;
import dev.marfien.rewibw.shared.world.EmptyChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class SetupCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (args.length < 3) {
            commandSender.sendMessage("Usage: /setup <map_name> <display_item> <display_name>");
            return true;
        }

        String mapName = args[0];
        String displayItem = args[1];
        String displayName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        String[] displayItemSplit = displayItem.split(":");
        Material material = Material.matchMaterial(displayItemSplit[0]);
        if (material == null) {
            commandSender.sendMessage("§cInvalid material: " + displayItemSplit[0]);
            return true;
        }

        int data = 0;
        if (displayItemSplit.length > 1) {
            try {
                data = Integer.parseInt(displayItemSplit[1]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage("§cInvalid data (NaN): " + displayItemSplit[1]);
                return true;
            }
        }

        commandSender.sendMessage("§aLoading setup session for map " + mapName + "...");

        try {
            FileUtils.copyFolder(SetupToolPlugin.getPluginConfig().getImportPath().resolve(mapName), Bukkit.getWorldContainer().toPath().resolve(mapName));
        } catch (IOException e) {
            commandSender.sendMessage("§cFailed to copy map files: " + e.getMessage());
            return true;
        }
        WorldCreator worldCreator = new WorldCreator(mapName)
                .environment(org.bukkit.World.Environment.NORMAL)
                .generateStructures(false)
                .type(org.bukkit.WorldType.FLAT)
                .generator(new EmptyChunkGenerator());

        Player player = (Player) commandSender;
        SetupSession session = SetupSessionManager.setSessions(player.getUniqueId(), worldCreator.createWorld());
        MapConfig config = session.getMapConfig();
        MapConfig.MapInfoConfig mapInfo = new MapConfig.MapInfoConfig();
        mapInfo.setDisplayName(displayName);
        mapInfo.setIcon(material + ":" + data);
        config.setMap(mapInfo);
        config.setTeams(new HashMap<>());
        player.teleport(new Location(session.getWorld(), 0, 120, 0));

        player.getInventory().setContents(GetItemsCommand.INVENTORY_CONTENTS);
        return false;
    }

}
