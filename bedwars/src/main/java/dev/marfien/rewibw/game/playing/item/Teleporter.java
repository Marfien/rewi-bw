package dev.marfien.rewibw.game.playing.item;

import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.CircleEffect;
import de.slikey.effectlib.util.ParticleEffect;
import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.shared.usable.ConsumeType;
import dev.marfien.rewibw.shared.usable.UsableItemInfo;
import dev.marfien.rewibw.team.GameTeam;
import dev.marfien.rewibw.team.TeamManager;
import dev.marfien.rewibw.util.Items;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class Teleporter extends UsableItemInfo {

    private static final Map<Player, BukkitTask> teleportingPlayers = new HashMap<>();
    private static final Map<CraftPlayer, Integer> launchedPlayers = new HashMap<>();

    private static final int SECONDS_TO_PREPARE = 5;

    public Teleporter() {
        super(ConsumeType.NONE);
        Bukkit.getScheduler().runTaskTimer(RewiBWPlugin.getInstance(), this::tick, 0, 1);
    }

    private void tick() {
        if (launchedPlayers.isEmpty()) return;

        for (Map.Entry<CraftPlayer, Integer> entry : launchedPlayers.entrySet()) {
            CraftPlayer player = entry.getKey();
            EntityPlayer nmsPlayer = player.getHandle();
            if (nmsPlayer.motY <= 0.0000000001) {
                launchedPlayers.remove(player);
                continue;
            }
            nmsPlayer.velocityChanged = true;
        }
    }

    @Override
    protected boolean onClick(PlayerInteractEvent event) {
        Player clicker = event.getPlayer();
        GameTeam team = TeamManager.getTeam(clicker);

        if (team == null) return false;

        if (teleportingPlayers.containsKey(clicker)) {
            clicker.sendMessage(RewiBWPlugin.PREFIX + Message.ALREADY_TELEPORTING);
            return false;
        }

        if (checkUnderBlocks(clicker.getLocation())) {
            clicker.sendMessage(RewiBWPlugin.PREFIX + Message.TELEPORTER_BELOW_BLOCK);
            return false;
        }

        BukkitTask task = new TeleporterTask(clicker)
                .runTaskTimer(RewiBWPlugin.getInstance(), 0, 20);
        teleportingPlayers.put(clicker, task);
        return true;
    }

    private static boolean checkUnderBlocks(Location location) {
        World world = location.getWorld();

        int blockY = location.getBlockY();

        int highestY1 = world.getHighestBlockYAt(
                Location.locToBlock(location.getX() + 0.2),
                Location.locToBlock(location.getZ() + 0.2)
        );
        int highestY2 = world.getHighestBlockYAt(
                Location.locToBlock(location.getX() - 0.2),
                Location.locToBlock(location.getZ() + 0.2)
        );
        int highestY3 = world.getHighestBlockYAt(
                Location.locToBlock(location.getX() + 0.2),
                Location.locToBlock(location.getZ() - 0.2)
        );
        int highestY4 = world.getHighestBlockYAt(
                Location.locToBlock(location.getX() - 0.2),
                Location.locToBlock(location.getZ() - 0.2)
        );

        return blockY < highestY1
                || blockY < highestY2
                || blockY < highestY3
                || blockY < highestY4;
    }

    public static class TeleporterListener implements Listener {

        @EventHandler
        private void onMove(PlayerMoveEvent event) {
            Player player = event.getPlayer();
            Location from = event.getFrom();
            Location to = event.getTo();

            Integer launchHeight = launchedPlayers.get(player);
            if (launchHeight != null && to.getY() > launchHeight + 150) {
                GameTeam team = TeamManager.getTeam(player);
                if (team == null) return;
                player.teleport(team.getSpawn());
                player.setVelocity(RewiBWPlugin.ZERO_VECTOR);
                launchedPlayers.remove(player);
                return;
            }

            if (!teleportingPlayers.containsKey(player)) return;

            if (to.getBlockX() == from.getBlockX()
                    && to.getBlockY() == from.getBlockY()
                    && to.getBlockZ() == from.getBlockZ()) {
                return;
            }

            player.sendMessage(RewiBWPlugin.PREFIX + Message.TELEPORT_CANCELLED);
            teleportingPlayers.remove(player).cancel();
        }

        @EventHandler
        private void onDeath(PlayerDeathEvent event) {
            Player player = event.getEntity();
            BukkitTask task = teleportingPlayers.remove(player);
            if (task != null) {
                task.cancel();
            }
        }

    }

    private static class TeleporterTask extends BukkitRunnable {

        private int remainingSeconds = SECONDS_TO_PREPARE;

        private final Player clicker;
        private final PlayerInventory inventory;

        public TeleporterTask(Player clicker) {
            this.clicker = clicker;
            this.inventory = clicker.getInventory();
        }

        @Override
        public void run() {
            Location location = this.clicker.getLocation();
            if (checkUnderBlocks(location)) {
                this.clicker.sendMessage(RewiBWPlugin.PREFIX + Message.TELEPORTER_BELOW_BLOCK);
                teleportingPlayers.remove(this.clicker);
                this.cancel();
                return;
            }

            if (!inventory.containsAtLeast(Items.TELEPORTER, 1)) {
                this.clicker.sendMessage(RewiBWPlugin.PREFIX + Message.TELEPORTER_LOST);
                teleportingPlayers.remove(this.clicker);
                super.cancel();
                return;
            }

            if (remainingSeconds == 0) {
                teleportingPlayers.remove(this.clicker);
                super.cancel();
                this.clicker.getInventory().removeItem(Items.TELEPORTER);
                this.clicker.setVelocity(new Vector(0, 200, 0));
                launchedPlayers.put((CraftPlayer) this.clicker, location.getBlockY());
                Bukkit.getScheduler().runTaskLater(RewiBWPlugin.getInstance(), () -> launchedPlayers.remove(this.clicker), 20 * 5);
                return;
            }

            CircleEffect effect = new CircleEffect(RewiBWPlugin.getEffectManager());
            effect.particle = ParticleEffect.FIREWORKS_SPARK;
            effect.type = EffectType.INSTANT;
            effect.radius = .75f;
            effect.iterations = 1;
            effect.wholeCircle = true;
            effect.enableRotation = false;
            effect.setLocation(location.add(0, 2.2, 0)
            );
            effect.start();
            for (Player player : this.clicker.getWorld().getPlayers()) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
            }
            remainingSeconds--;
        }
    }

}
