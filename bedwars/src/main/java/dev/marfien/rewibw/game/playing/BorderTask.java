package dev.marfien.rewibw.game.playing;

import dev.marfien.rewibw.shared.config.MapConfig;
import dev.marfien.rewibw.world.MapWorld;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.scheduler.BukkitRunnable;

public class BorderTask extends BukkitRunnable {

    private static final int BORDER_DISTANCE_VISIBLE = 10;
    private static final EnumParticle PARTICLE_EFFECT = EnumParticle.SMOKE_NORMAL;
    private static final float BORDER_DISPLACE_WIDTH = 6.5F;
    private static final float BORDER_DISPLACE_HEIGHT = 5F;

    private final MapConfig.BorderSnapshot border;
    private final World world;

    public BorderTask(MapWorld mapWorld) {
        this.world = ((CraftWorld) mapWorld.getWorld()).getHandle();
        this.border = mapWorld.getConfig().getMap().getBorder().getSnapshot();
    }

    @Override
    public void run() {
        for (EntityHuman human : this.world.players) {
            if (!(human instanceof EntityPlayer)) continue;
            EntityPlayer player = (EntityPlayer) human;

            if (Math.abs(player.locX - this.border.getLowerX()) < BORDER_DISTANCE_VISIBLE) {
                drawBorderX(player, this.border.getLowerX(), Location.locToBlock(player.locY) + 0.5F, Location.locToBlock(player.locZ) + 0.5F);
            }

            if (Math.abs(player.locX - this.border.getUpperX() + 1) < BORDER_DISTANCE_VISIBLE) {
                drawBorderX(player, this.border.getUpperX() + 1, Location.locToBlock(player.locY) + 0.5F, Location.locToBlock(player.locZ) + 0.5F);
            }

            if (Math.abs(player.locZ - this.border.getLowerZ()) < BORDER_DISTANCE_VISIBLE) {
                drawBorderZ(player, Location.locToBlock(player.locX) + 0.5F, Location.locToBlock(player.locY) + 0.5F, this.border.getLowerZ());
            }

            if (Math.abs(player.locZ - this.border.getUpperZ() + 1) < BORDER_DISTANCE_VISIBLE) {
                drawBorderZ(player, Location.locToBlock(player.locX) + 0.5F, Location.locToBlock(player.locY) + 0.5F, this.border.getUpperZ() + 1);
            }
        }
    }

    private static void drawBorderX(EntityPlayer player, float x, float y, float z) {
        for (float yOffset = -BORDER_DISPLACE_HEIGHT; yOffset < BORDER_DISPLACE_HEIGHT; yOffset += 0.5F) {
            for (float zOffset = -BORDER_DISPLACE_WIDTH; zOffset < BORDER_DISPLACE_WIDTH; zOffset += 0.5F) {
                spawnParticle(player, x, y + yOffset, z + zOffset);
            }
        }
    }

    private static void drawBorderZ(EntityPlayer player, float x, float y, float z) {
        for (float yOffset = -BORDER_DISPLACE_HEIGHT; yOffset < BORDER_DISPLACE_HEIGHT; yOffset += 0.5F) {
            for (float xOffset = -BORDER_DISPLACE_WIDTH; xOffset < BORDER_DISPLACE_WIDTH; xOffset += 0.5F) {
                spawnParticle(player, x + xOffset, y + yOffset, z);
            }
        }
    }

    private static void spawnParticle(EntityPlayer player, float x, float y, float z) {
        player.playerConnection.sendPacket(
                new PacketPlayOutWorldParticles(PARTICLE_EFFECT, false, x, y, z, 0, 0, 0, 0, 0));
    }

}
