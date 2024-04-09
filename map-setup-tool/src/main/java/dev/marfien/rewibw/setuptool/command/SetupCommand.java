package dev.marfien.rewibw.setuptool.command;

import dev.marfien.rewibw.setuptool.SetupSession;
import dev.marfien.rewibw.setuptool.SetupSessionManager;
import dev.marfien.rewibw.setuptool.SetupToolPlugin;
import dev.marfien.rewibw.shared.CustomCommand;
import dev.marfien.rewibw.shared.FileUtils;
import dev.marfien.rewibw.shared.config.MapConfig;
import dev.marfien.rewibw.shared.world.EmptyChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class SetupCommand implements CustomCommand {

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cOnly players can use this command.");
            return;
        }

        if (args.length < 2) {
            commandSender.sendMessage("Usage: /setup <map_name> <display_name>");
            commandSender.sendMessage("You have to hold the display item in your hand.");
            return;
        }

        String mapName = args[0];
        String displayName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        ItemStack inHand = ((Player) commandSender).getItemInHand();
        if (inHand.getType() == Material.AIR) {
            commandSender.sendMessage("§cYou are not holding an Item in your hand.");
            return;
        }

        commandSender.sendMessage("§aLoading setup session for map " + mapName + "...");

        try {
            FileUtils.copyFolder(SetupToolPlugin.getPluginConfig().getImportPath().resolve(mapName), Bukkit.getWorldContainer().toPath().resolve(mapName));
        } catch (IOException e) {
            commandSender.sendMessage("§cFailed to copy map files: " + e.getMessage());
            return;
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
        mapInfo.setIcon(inHand.getType() + ":" + inHand.getDurability());
        config.setMap(mapInfo);
        config.setTeams(new HashMap<>());
        player.teleport(new Location(session.getWorld(), 0, 120, 0));

        player.getInventory().setContents(GetItemsCommand.contents);
    }

}
