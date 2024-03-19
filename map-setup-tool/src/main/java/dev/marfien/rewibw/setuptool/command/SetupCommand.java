package dev.marfien.rewibw.setuptool.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class SetupCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
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
        WorldCreator worldCreator = new WorldCreator(mapName)
                .environment(org.bukkit.World.Environment.NORMAL)
                .generateStructures(false)
                .type(org.bukkit.WorldType.FLAT)
                .generator();

        return false;
    }

}
