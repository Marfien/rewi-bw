package dev.marfien.rewibw.game.playing.listener;

import com.google.common.collect.Sets;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MapProtectionListener implements Listener {

    public static final Collection<Material> BREAKABLE_BLOCKS = Sets.newHashSet(
            Material.LONG_GRASS,
            Material.DEAD_BUSH,
            Material.DOUBLE_PLANT,
            Material.RED_ROSE,
            Material.YELLOW_FLOWER
    );

    private static final Set<Block> placedByPlayer = new HashSet<>();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onBlockPlace(BlockPlaceEvent event) {
        placedByPlayer.add(event.getBlock());
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (BREAKABLE_BLOCKS.contains(block.getType())) {
            return;
        }

        if (!placedByPlayer.remove(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onExplosion(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> !placedByPlayer.remove(block));
    }

    @EventHandler
    private void onExplosion(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> !placedByPlayer.remove(block));
    }

    public static boolean isPlacedByPlayer(Block block) {
        return placedByPlayer.contains(block);
    }

}
