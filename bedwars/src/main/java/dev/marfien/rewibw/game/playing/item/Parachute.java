package dev.marfien.rewibw.game.playing.item;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.shared.usable.ConsumeType;
import dev.marfien.rewibw.shared.usable.UsableItemInfo;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class Parachute extends UsableItemInfo {

    private static final Map<Player, BukkitTask> activeParachutes = new HashMap<>();

    public Parachute() {
        super(ConsumeType.DECREASE_AMOUNT);
        Bukkit.getScheduler().runTaskTimer(RewiBWPlugin.getInstance(), this::tick, 0, 1);
    }

    @Override
    protected boolean onClick(PlayerInteractEvent event) {
        Player clicker = event.getPlayer();

        if (clicker.isOnGround()) {
            clicker.sendMessage(RewiBWPlugin.PREFIX + Message.PARACHUTE_ON_GROUND);
            return false;
        }

        if (activeParachutes.containsKey(clicker)) {
            clicker.sendMessage(RewiBWPlugin.PREFIX + Message.PARACHUTE_ALREADY_ACTIVE);
            return false;
        }


        clicker.setPassenger(clicker.getWorld().spawnEntity(clicker.getEyeLocation(), EntityType.CHICKEN));

        clicker.setVelocity(clicker.getVelocity().setY(0));
        activeParachutes.put(clicker, Bukkit.getScheduler().runTaskLater(RewiBWPlugin.getInstance(), () -> {
            clicker.sendMessage(RewiBWPlugin.PREFIX + Message.PARACHUTE_BROKE);
            removeParachute(clicker);
        }, 30 * 20));
        return true;
    }

    private static void removeParachute(Player player) {
        Entity parachute = player.getPassenger();
        parachute.getWorld().playEffect(parachute.getLocation(), Effect.LARGE_SMOKE, 1);
        parachute.remove();
        BukkitTask task = activeParachutes.remove(player);
        if (task != null) task.cancel();
    }

    private void tick() {
        if (activeParachutes.isEmpty()) return;

        for (Player player : activeParachutes.keySet()) {
            EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

            if (nmsPlayer.onGround || nmsPlayer.motY >= 0.0) continue;

            float walkingSpeed = player.getWalkSpeed();
            double rotX = nmsPlayer.yaw;
            double rotY = nmsPlayer.pitch;
            double xz = Math.cos(Math.toRadians(rotY));
            double directionX = -xz * Math.sin(Math.toRadians(rotX));
            double directionZ = xz * Math.cos(Math.toRadians(rotX));

            nmsPlayer.motY *= 0.8;
            nmsPlayer.motX = directionX * walkingSpeed * 2;
            nmsPlayer.motZ = directionZ * walkingSpeed * 2;
            nmsPlayer.velocityChanged = true;
            nmsPlayer.fallDistance = 0f;
        }
    }

    public static class ParachuteListener implements Listener {

        @EventHandler
        private void onMove(PlayerMoveEvent event) {
            Player player = event.getPlayer();

            if (player.isEmpty()) return;
            if (!activeParachutes.containsKey(player)) return;

            if (player.isOnGround()) {
                removeParachute(player);
            }
        }

        @EventHandler
        private void onParachuteDeath(EntityDeathEvent event) {
            Entity entity = event.getEntity();
            Entity vehicle = entity.getVehicle();
            BukkitTask task = activeParachutes.remove(vehicle);
            if (task != null) {
                task.cancel();
                event.setDroppedExp(0);
                event.getDrops().clear();
            }
        }

        @EventHandler
        private void onPlayerDeath(PlayerDeathEvent event) {
            Player player = event.getEntity();

            if (player.isEmpty()) return;
            if (!activeParachutes.containsKey(player)) return;
            removeParachute(player);
        }
    }
}