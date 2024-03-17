package dev.marfien.rewibw.game.playing;

import de.slikey.effectlib.util.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BorderTask extends BukkitRunnable {

    private static final int BORDER_DISTANCE_VISIBLE = 10;
    private static final ParticleEffect PARTICLE_EFFECT = ParticleEffect.TOWN_AURA;
    private static final float BORDER_DISPLACE_WIDTH = 6.5F;
    private static final float BORDER_DISPLACE_HEIGHT = 5F;

    private final int borderX1 = PlayingGameState.getMap().getBorderLowerX();
    private final int borderX2 = PlayingGameState.getMap().getBorderUpperX();

    private final int borderZ1 = PlayingGameState.getMap().getBorderLowerZ();
    private final int borderZ2 = PlayingGameState.getMap().getBorderUpperZ();

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Location location = player.getLocation();

            // TODO: Add one on positive
            if (Math.abs(location.getX() - borderX1) < BORDER_DISTANCE_VISIBLE) {
                drawBorderX(player, borderX1, location.getY(), location.getZ());
            }

            if (Math.abs(location.getX() - borderX2) < BORDER_DISTANCE_VISIBLE) {
                drawBorderX(player, borderX2, location.getY(), location.getZ());
            }

            if (Math.abs(location.getZ() - borderZ1) < BORDER_DISTANCE_VISIBLE) {
                drawBorderZ(player, location.getX(), location.getY(), borderZ1);
            }

            if (Math.abs(location.getZ() - borderZ2) < BORDER_DISTANCE_VISIBLE) {
                drawBorderZ(player, location.getX(), location.getY(), borderZ2);
            }
        }
    }

    private static void drawBorderX(Player player, double x, double y, double z) {
        for (float yOffset = -BORDER_DISPLACE_HEIGHT; yOffset < BORDER_DISPLACE_HEIGHT; yOffset += 0.5F) {
            Location location = new Location(player.getWorld(), x, y + yOffset, z);
            for (float zOffset = -BORDER_DISPLACE_WIDTH; zOffset < BORDER_DISPLACE_WIDTH; zOffset += 0.5F) {
                location.setZ(z + zOffset);
                PARTICLE_EFFECT.display(0, 0, 0, 0, 1, location, player);
            }
        }
    }

    private static void drawBorderZ(Player player, double x, double y, double z) {
        for (float yOffset = -BORDER_DISPLACE_HEIGHT; yOffset < BORDER_DISPLACE_HEIGHT; yOffset += 0.5F) {
            Location location = new Location(player.getWorld(), x, y + yOffset, z);
            for (float xOffset = -BORDER_DISPLACE_WIDTH; xOffset < BORDER_DISPLACE_WIDTH; xOffset += 0.5F) {
                location.setX(x + xOffset);
                PARTICLE_EFFECT.display(0, 0, 0, 0, 1, location, player);
            }
        }
    }

}
