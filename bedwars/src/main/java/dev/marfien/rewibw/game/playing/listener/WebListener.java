package dev.marfien.rewibw.game.playing.listener;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.team.GameTeam;
import dev.marfien.rewibw.team.TeamBed;
import dev.marfien.rewibw.team.TeamManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class WebListener implements Listener {

    private final Map<Player, Long> lastWebPlace = new HashMap<>();
    private final Map<Block, BukkitTask> removeTaskByBlock = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onWebPlace(BlockPlaceEvent event) {
        Block placed = event.getBlockPlaced();
        if (placed.getType() != Material.WEB) return;

        for (GameTeam team : TeamManager.getTeams()) {
            TeamBed bed = team.getBed();
            if (bed.isAlive() && bed.distanceSquared(placed.getLocation()) < 2.01) {
                RewiBWPlugin.getPluginLogger().debug("Web placed near bed");
                return;
            }
        }

        Player player = event.getPlayer();
        long lastPlace = this.lastWebPlace.getOrDefault(player, 0L);
        if (System.currentTimeMillis() - lastPlace < 20_000) {
            event.setCancelled(true);
            player.sendMessage(RewiBWPlugin.PREFIX + Message.WEB_COOLDOWN);
            return;
        }

        this.lastWebPlace.put(player, System.currentTimeMillis());
        this.removeTaskByBlock.put(placed, RewiBWPlugin.getScheduler().runTaskLater(() -> {
            event.getBlockReplacedState().update(true, false);
            this.removeTaskByBlock.remove(placed);
        }, 20 * 10L));
    }

    @EventHandler
    public void onWebBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.WEB) return;

        BukkitTask task = this.removeTaskByBlock.remove(block);
        if (task != null) {
            task.cancel();
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (block.getType() == Material.WEB) {
                BukkitTask task = this.removeTaskByBlock.remove(block);
                if (task != null) {
                    task.cancel();
                }
            }
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (block.getType() == Material.WEB) {
                BukkitTask task = this.removeTaskByBlock.remove(block);
                if (task != null) {
                    task.cancel();
                }
            }
        }
    }

}
