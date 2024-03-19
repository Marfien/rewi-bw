package dev.marfien.rewibw.setuptool.command;

import dev.marfien.rewibw.setuptool.SetupSession;
import dev.marfien.rewibw.setuptool.SetupToolPlugin;
import dev.marfien.rewibw.shared.world.EmptyChunkGenerator;
import dev.marfien.rewibw.shared.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

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
        WorldCreator worldCreator = new WorldCreator(mapName)
                .environment(org.bukkit.World.Environment.NORMAL)
                .generateStructures(false)
                .type(org.bukkit.WorldType.FLAT)
                .generator(new EmptyChunkGenerator());

        Player player = (Player) commandSender;
        SetupToolPlugin.setSession(player, new SetupSession(worldCreator.createWorld(), displayName, material + ":" + data));
        player.teleport(new Location(player.getWorld(), 0, 120, 0));

        Inventory inventory = player.getInventory();
        inventory.clear();
        inventory.setItem(0, ItemBuilder.of(Material.HARD_CLAY).setDisplayName("§aBronze-Spawner hinzufügen").asItemStack());
        inventory.setItem(1, ItemBuilder.of(Material.IRON_BLOCK).setDisplayName("§aEisen-Spawner hinzufügen").asItemStack());
        inventory.setItem(2, ItemBuilder.of(Material.GOLD_BLOCK).setDisplayName("§aGold-Spawner hinzufügen").asItemStack());

        inventory.setItem(4, ItemBuilder.of(Material.ARMOR_STAND).setDisplayName("§aShop hinzufügen").asItemStack());
        inventory.setItem(5, ItemBuilder.of(Material.GLASS).setDisplayName("§aSpectator-Spawn setzen").asItemStack());

        inventory.setItem(7, ItemBuilder.of(Material.BANNER).setDisplayName("§aTeam hinzufügen").asItemStack());
        inventory.setItem(8, ItemBuilder.of(Material.REDSTONE_BLOCK).setDisplayName("§cPosition entfernen").asItemStack());
        return false;
    }

}
