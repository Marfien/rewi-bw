package dev.marfien.rewibw.game.playing;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.scheduler.BukkitRunnable;

public class BorderTask extends BukkitRunnable {

    private static final int BORDER_DISTANCE_VISIBLE = 10;
    private static final EnumParticle PARTICLE_EFFECT = EnumParticle.SMOKE_NORMAL;
    private static final float BORDER_DISPLACE_WIDTH = 6.5F;
    private static final float BORDER_DISPLACE_HEIGHT = 5F;

    private final float borderLowerX = PlayingGameState.getMap().getBorderLowerX();
    private final float borderUpperX = PlayingGameState.getMap().getBorderUpperX() + 1;

    private final float borderLowerZ = PlayingGameState.getMap().getBorderLowerZ();
    private final float borderUpperZ = PlayingGameState.getMap().getBorderUpperZ() + 1;
    private final World world = ((CraftWorld) PlayingGameState.getMap().getWorld()).getHandle();

    @Override
    public void run() {
        for (EntityHuman human : this.world.players) {
            if (!(human instanceof EntityPlayer)) continue;
            EntityPlayer player = (EntityPlayer) human;

            if (Math.abs(player.locX - borderLowerX) < BORDER_DISTANCE_VISIBLE) {
                drawBorderX(player, borderLowerX, Location.locToBlock(player.locY) + 0.5F, Location.locToBlock(player.locZ) + 0.5F);
            }

            if (Math.abs(player.locX - borderUpperX) < BORDER_DISTANCE_VISIBLE) {
                drawBorderX(player, borderUpperX, Location.locToBlock(player.locY) + 0.5F, Location.locToBlock(player.locZ) + 0.5F);
            }

            if (Math.abs(player.locZ - borderLowerZ) < BORDER_DISTANCE_VISIBLE) {
                drawBorderZ(player, Location.locToBlock(player.locX) + 0.5F, Location.locToBlock(player.locY) + 0.5F, borderLowerZ);
            }

            if (Math.abs(player.locZ - borderUpperZ) < BORDER_DISTANCE_VISIBLE) {
                drawBorderZ(player, Location.locToBlock(player.locX) + 0.5F, Location.locToBlock(player.locY) + 0.5F, borderUpperZ);
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
                new PacketPlayOutWorldParticles(PARTICLE_EFFECT, false, x, y, z, 0, 0, 0, 1, 1));
    }

}
