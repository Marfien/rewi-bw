package dev.marfien.rewibw.setuptool.listener;

import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.CubeEffect;
import de.slikey.effectlib.effect.LineEffect;
import de.slikey.effectlib.util.ParticleEffect;
import dev.marfien.rewibw.setuptool.SetupSession;
import dev.marfien.rewibw.setuptool.SetupToolPlugin;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collection;

public class SetupListener implements Listener {

    private static Location toCleanLocation(Location location) {
        return new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY(), location.getBlockZ() + 0.5);
    }

    private static void addLocationEffect(Location location, ParticleEffect particleEffect, Color color, Material material) {
        Location blockLocation = location.getBlock().getLocation();
        CubeEffect effect = new CubeEffect(SetupToolPlugin.getEffectManager());
        effect.type = EffectType.REPEATING;
        effect.iterations = -1;
        effect.particle = particleEffect;
        effect.color = color;
        effect.material = material;
        effect.setLocation(blockLocation);
        effect.edgeLength = 1;
        effect.outlineOnly = true;
        SetupToolPlugin.effects.add(effect);
        effect.start();
    }

    @EventHandler
    private void onPlace(PlayerInteractEvent event) {
        if (!event.hasBlock()) return;
        if (!event.hasItem()) return;

        Player player = event.getPlayer();
        SetupSession session = SetupToolPlugin.getSession(player);

        if (session == null) return;
        Block targetBlock = event.getClickedBlock().getRelative(event.getBlockFace());
        Location location = toCleanLocation(targetBlock.getLocation());

        boolean cancel = true;
        switch (event.getItem().getType()) {
            case HARD_CLAY:
                session.getBronzeSpawns().add(location);
                addLocationEffect(location, ParticleEffect.BLOCK_CRACK, null, Material.HARD_CLAY);
                player.sendMessage("§aBronze-Spawner added.");
                break;
            case IRON_BLOCK:
                session.getSilverSpawns().add(location);
                addLocationEffect(location, ParticleEffect.BLOCK_CRACK, null, Material.IRON_BLOCK);
                player.sendMessage("§aSilver-Spawner added.");
                break;
            case GOLD_BLOCK:
                session.getGoldSpawns().add(location);
                addLocationEffect(location, ParticleEffect.REDSTONE, null, Material.GOLD_BLOCK);
                player.sendMessage("§aGold-Spawner added.");
                break;
            case ARMOR_STAND:
                location.setDirection(location.clone().subtract(player.getLocation()).toVector());
                location.setPitch(0);
                location.setYaw(Math.round(location.getYaw() / 45) * 45F);

                session.getShops().add(location);
                addLocationEffect(location, ParticleEffect.VILLAGER_HAPPY, null, null);
                player.sendMessage("§aShop added.");

                LineEffect effect = new LineEffect(SetupToolPlugin.getEffectManager());
                effect.setLocation(location);
                effect.particle = ParticleEffect.VILLAGER_HAPPY;
                effect.length = 1;
                effect.type = EffectType.REPEATING;
                effect.iterations = -1;

                break;
            case GLASS:
                location.setPitch(90);
                session.setSpawn(location);
                addLocationEffect(location, ParticleEffect.FLAME, null, null);
                player.sendMessage("§aSpectator-Spawn set.");
                break;
            default:
                cancel = false;
                break;
        }

        if (cancel) event.setCancelled(true);
    }

    @EventHandler
    private void onLocationRemove(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        SetupSession session = SetupToolPlugin.getSession(player);

        if (session == null) return;
        if (event.getBlockPlaced().getType() != Material.REDSTONE_BLOCK) return;

        Location location = event.getBlock().getLocation();
        event.setCancelled(true);

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
