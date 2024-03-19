package dev.marfien.rewibw.setuptool.item;

import dev.marfien.rewibw.setuptool.SetupSession;
import dev.marfien.rewibw.setuptool.SetupToolPlugin;
import dev.marfien.rewibw.shared.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class LocationRemover extends SessionItem {

    public static final ItemStack ITEM = ItemBuilder.of(Material.REDSTONE_BLOCK).setDisplayName("§cPosition entfernen").asItemStack();

    @Override
    protected void onClick(PlayerInteractEvent event, Player player, SetupSession session, Location location) {
        if (isSameBlock(location, session.getSpawn())) {
            player.sendMessage("§cSpectator-Spawn removed.");
            session.setSpawn(null);
        }

        Collection<Location> bronzeSpawns = session.getBronzeSpawns();
        int bronzeSpawnsSize = bronzeSpawns.size();
        bronzeSpawns.removeIf(bronzeSpawn -> isSameBlock(location, bronzeSpawn));
        if (bronzeSpawns.size() != bronzeSpawnsSize) {
            player.sendMessage("§cRemoved " + (bronzeSpawnsSize - bronzeSpawns.size()) + " bronze spawn(s).");
        }

        Collection<Location> silverSpawns = session.getSilverSpawns();
        int silverSpawnsSize = silverSpawns.size();
        silverSpawns.removeIf(silverSpawn -> isSameBlock(location, silverSpawn));
        if (silverSpawns.size() != silverSpawnsSize) {
            player.sendMessage("§cRemoved " + (silverSpawnsSize - silverSpawns.size()) + " silver spawn(s).");
        }

        Collection<Location> goldSpawns = session.getGoldSpawns();
        int goldSpawnsSize = goldSpawns.size();
        goldSpawns.removeIf(goldSpawn -> isSameBlock(location, goldSpawn));
        if (goldSpawns.size() != goldSpawnsSize) {
            player.sendMessage("§cRemoved " + (goldSpawnsSize - goldSpawns.size()) + " gold spawn(s).");
        }

        Collection<Location> shops = session.getShops();
        int shopsSize = shops.size();
        shops.removeIf(shop -> isSameBlock(location, shop));
        if (shops.size() != shopsSize) {
            player.sendMessage("§cRemoved " + (shopsSize - shops.size()) + " shop(s).");
        }

        SetupToolPlugin.effects.removeIf(effect -> {
            if (isSameBlock(effect.getLocation(), location)) {
                effect.cancel();
                return true;
            } else {
                return false;
            }
        });

        // TODO remove location from team info
    }

    private static boolean isSameBlock(Location location1, Location location2) {
        if (location1 == null || location2 == null) return false;
        return location1.getBlockX() == location2.getBlockX() && location1.getBlockY() == location2.getBlockY() && location1.getBlockZ() == location2.getBlockZ() && location1.getWorld().equals(location2.getWorld());
    }

}
