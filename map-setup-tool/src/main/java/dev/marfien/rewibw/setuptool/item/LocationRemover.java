package dev.marfien.rewibw.setuptool.item;

import dev.marfien.rewibw.setuptool.SetupToolPlugin;
import dev.marfien.rewibw.shared.ItemBuilder;
import dev.marfien.rewibw.shared.Position;
import dev.marfien.rewibw.shared.config.MapConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class LocationRemover extends SessionItem {

    public static final ItemStack ITEM = ItemBuilder.of(Material.REDSTONE_BLOCK).setDisplayName("§cPosition entfernen").asItemStack();

    @Override
    protected void onClick(PlayerInteractEvent event, Player player, MapConfig mapConfig, Location location) {
        if (isSameBlock(location, mapConfig.getSpectatorSpawn())) {
            player.sendMessage("§cSpectator-Spawn removed.");
            mapConfig.setSpectatorSpawn(null);
        }

        // The ArrayList created in Arrays.asList cannot be modified in its size, so we create a new ArrayList from it
        Collection<Position> bronzeSpawns = new ArrayList<>(Arrays.asList(mapConfig.getSpawner().getBronze()));
        int bronzeSpawnsSize = bronzeSpawns.size();
        bronzeSpawns.removeIf(bronzeSpawn -> isSameBlock(location, bronzeSpawn));
        if (bronzeSpawns.size() != bronzeSpawnsSize) {
            mapConfig.getSpawner().setBronze(bronzeSpawns.toArray(new Position[0]));
            player.sendMessage("§cRemoved " + (bronzeSpawnsSize - bronzeSpawns.size()) + " bronze spawn(s).");
        }

        Collection<Position> silverSpawns = new ArrayList<>(Arrays.asList(mapConfig.getSpawner().getSilver()));
        int silverSpawnsSize = silverSpawns.size();
        silverSpawns.removeIf(silverSpawn -> isSameBlock(location, silverSpawn));
        if (silverSpawns.size() != silverSpawnsSize) {
            mapConfig.getSpawner().setSilver(silverSpawns.toArray(new Position[0]));
            player.sendMessage("§cRemoved " + (silverSpawnsSize - silverSpawns.size()) + " silver spawn(s).");
        }

        Collection<Position> goldSpawns = new ArrayList<>(Arrays.asList(mapConfig.getSpawner().getGold()));
        int goldSpawnsSize = goldSpawns.size();
        goldSpawns.removeIf(goldSpawn -> isSameBlock(location, goldSpawn));
        if (goldSpawns.size() != goldSpawnsSize) {
            mapConfig.getSpawner().setGold(goldSpawns.toArray(new Position[0]));
            player.sendMessage("§cRemoved " + (goldSpawnsSize - goldSpawns.size()) + " gold spawn(s).");
        }

        Collection<Position> shops = new ArrayList<>(Arrays.asList(mapConfig.getShops()));
        int shopsSize = shops.size();
        shops.removeIf(shop -> isSameBlock(location, shop));
        if (shops.size() != shopsSize) {
            mapConfig.setShops(shops.toArray(new Position[0]));
            player.sendMessage("§cRemoved " + (shopsSize - shops.size()) + " shop(s).");
        }

        mapConfig.getTeams().forEach((color, team) -> {
            if (isSameBlock(location, team.getSpawn())) {
                team.setSpawn(null);
                player.sendMessage("§cRemoved spawn of team " + color.getDisplayName() + "§c.");
            }

            MapConfig.TeamBedConfig bed = team.getBed();
            if (bed == null) return;

            if (location.getBlockX() == bed.getX() && location.getBlockY() == bed.getY() && location.getBlockZ() == bed.getZ()) {
                team.setBed(null);
                player.sendMessage("§cRemoved bed of team " + color.getDisplayName() + "§c.");
            }
        });

        SetupToolPlugin.getEffects().removeIf(effect -> {
            if (isSameBlock(effect.getLocation(), location)) {
                effect.cancel();
                return true;
            } else {
                return false;
            }
        });
    }

    private static boolean isSameBlock(Location location1, Position position) {
        if (location1 == null || position == null) return false;
        return location1.getBlockX() == position.getBlockX() && location1.getBlockY() == position.getBlockY() && location1.getBlockZ() == position.getBlockZ();
    }

    private static boolean isSameBlock(Location location1, Location location2) {
        if (location1 == null || location2 == null) return false;
        return location1.getBlockX() == location2.getBlockX() && location1.getBlockY() == location2.getBlockY() && location1.getBlockZ() == location2.getBlockZ();
    }

}
