package dev.marfien.rewibw.game.playing.listener;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.playing.PlayingGameState;
import dev.marfien.rewibw.team.GameTeam;
import dev.marfien.rewibw.team.TeamChest;
import dev.marfien.rewibw.team.TeamManager;
import dev.marfien.rewibw.util.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void adjustBlockDrop(ItemSpawnEvent event) {
        Item item = event.getEntity();
        Material type = item.getItemStack().getType();

        if (MapProtectionListener.BREAKABLE_BLOCKS.contains(type)) {
            event.setCancelled(true);
            return;
        }

        int amount = item.getItemStack().getAmount();

        ItemStack itemStack;
        switch (type) {
            case RED_SANDSTONE:
                itemStack = Items.RED_SANDSTONE.clone();
                break;
            case ENDER_STONE:
                itemStack = Items.ENDSTONE.clone();
                break;
            case IRON_BLOCK:
                itemStack = Items.IRON_BLOCK.clone();
                break;
            case CHEST:
                itemStack = Items.CHEST.clone();
                break;
            case LADDER:
                itemStack = Items.LADDER.clone();
                break;
            case TNT:
                itemStack = Items.TNT.clone();
                break;
            default:
                return;
        }

        itemStack.setAmount(amount);
        item.setItemStack(itemStack);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlaceOutOfMap(BlockPlaceEvent event) {
        GameMap map = PlayingGameState.getMap();
        Block block = event.getBlockPlaced();

        if (block.getX() <= map.getBorderUpperX() && block.getX() >= map.getBorderLowerX()
                && block.getZ() <= map.getBorderUpperZ() && block.getZ() >= map.getBorderLowerZ()) {
            return;
        }
        event.setCancelled(true);
        event.getPlayer().sendMessage(RewiBWPlugin.PREFIX + Message.BLOCK_OUT_OF_MAP);
    }

    @EventHandler(ignoreCancelled = true)
    private void onTeamChestPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (block.getType() != Material.ENDER_CHEST) return;

        GameTeam team = TeamManager.getTeam(event.getPlayer());
        if (team == null) return;

        Bukkit.getPluginManager().registerEvents(new TeamChest(block, team), RewiBWPlugin.getInstance());
    }

    @EventHandler(ignoreCancelled = true)
    private void onTNTPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (block.getType() != Material.TNT) return;

        block.setType(Material.AIR);
        TNTPrimed tnt = block.getWorld().spawn(block.getLocation().add(0.5, 0.0, 0.5), TNTPrimed.class);
        tnt.setVelocity(RewiBWPlugin.ZERO_VECTOR);
    }

}
