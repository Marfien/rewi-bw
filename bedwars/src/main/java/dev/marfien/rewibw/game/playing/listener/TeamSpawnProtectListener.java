package dev.marfien.rewibw.game.playing.listener;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.team.GameTeam;
import dev.marfien.rewibw.team.TeamManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class TeamSpawnProtectListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        Block blockBelow = block.getRelative(BlockFace.DOWN);

        for (GameTeam team : TeamManager.getTeams()) {
            if (!team.getBed().isAlive()) continue;
            Block spawnBlock = team.getSpawn().getBlock();

            if (block.equals(spawnBlock) || blockBelow.equals(spawnBlock)) {
                event.getPlayer().sendMessage("§8[§2HyPro§8] " + Message.CANNOT_PLACE_BLOCKS_TEAMSPAWN);
                event.setCancelled(true);
                return;
            }
        }
    }

}
