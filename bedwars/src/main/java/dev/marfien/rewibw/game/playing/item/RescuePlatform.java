package dev.marfien.rewibw.game.playing.item;

import dev.marfien.rewibw.game.playing.listener.MapProtectionListener;
import dev.marfien.rewibw.team.GameTeam;
import dev.marfien.rewibw.team.TeamManager;
import dev.marfien.rewibw.shared.usable.ConsumeType;
import dev.marfien.rewibw.shared.usable.UsableItemInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;

public class RescuePlatform extends UsableItemInfo {

    private static final float RADIUS = 2.5f;
    private static final Collection<Vector> platformOffset = new ArrayList<>();

    static {
        for (int x = NumberConversions.ceil(0.5 - RADIUS); x < NumberConversions.floor(0.5 + RADIUS); x++) {
            for (int z = NumberConversions.ceil(0.5 - RADIUS); z < NumberConversions.floor(0.5 + RADIUS); z++) {
                if (x * x + z * z > RADIUS * RADIUS) continue;
                platformOffset.add(new Vector(x, -1, z));
            }
        }
    }

    public RescuePlatform() {
        super(ConsumeType.DECREASE_AMOUNT);
    }

    @Override
    protected boolean onClick(PlayerInteractEvent event) {
        Player clicker = event.getPlayer();
        GameTeam team = TeamManager.getTeam(clicker);

        if (team == null) return false;

        Location center = clicker.getLocation();

        World world = center.getWorld();
        for (Vector vector : platformOffset) {
            Block block = world.getBlockAt(
                    center.getBlockX() + vector.getBlockX(),
                    center.getBlockY() + vector.getBlockY(),
                    center.getBlockZ() + vector.getBlockZ()
            );

            if (block.getType() != Material.AIR
                    && !MapProtectionListener.BREAKABLE_BLOCKS.contains(block.getType())) continue;

            block.setType(Material.STAINED_GLASS);
            block.setData(team.getColor().getDyeColor().getData());
        }

        return true;
    }
}
