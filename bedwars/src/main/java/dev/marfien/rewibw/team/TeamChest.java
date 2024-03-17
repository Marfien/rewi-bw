package dev.marfien.rewibw.team;

import de.slikey.effectlib.util.ParticleEffect;
import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockAction;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashSet;

public class TeamChest implements Listener {

    private static final net.minecraft.server.v1_8_R3.Block ENDER_CHEST_NMS = net.minecraft.server.v1_8_R3.Block.getById(Material.ENDER_CHEST.getId());
    private static final MaterialData ENDER_CHEST_DATA = new MaterialData(Material.ENDER_CHEST);

    private final Collection<Player> viewers = new HashSet<>();

    private final Block chest;
    private final GameTeam owningTeam;
    private final BukkitTask timerTask;

    private final BlockPosition blockPosition;

    public TeamChest(Block chest, GameTeam owningTeam) {
        this.chest = chest;
        this.blockPosition = new BlockPosition(this.chest.getX(), this.chest.getY(), this.chest.getZ());
        this.owningTeam = owningTeam;
        this.timerTask = this.startTimerTask();
    }

    private BukkitTask startTimerTask() {
        Location blockCenter = this.chest.getLocation().add(0.5, 0.0, 0.5);
        return Bukkit.getScheduler().runTaskTimerAsynchronously(RewiBWPlugin.getInstance(), () -> {
            this.updateViewerCount();

            for (int i = 0; i < 5; i++) {
                ParticleEffect.SPELL_MOB.display(blockCenter, this.owningTeam.getColor().getDyeColor().getColor(), 12);
            }
        }, 0, 10);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (!this.chest.equals(block)) return;

        Player player = event.getPlayer();
        if (player.isSneaking() && event.getItem() != null) {
            return;
        }

        if (!this.owningTeam.isMember(player)) return;

        if (this.viewers.add(player)) {
            this.updateViewerCount();
        }

        player.openInventory(this.owningTeam.getTeamChest());
        event.setCancelled(true);
    }

    public void destroy() {
        this.viewers.forEach(Player::closeInventory);
        this.viewers.clear();
        HandlerList.unregisterAll(this);
        this.timerTask.cancel();

        Location location = this.chest.getLocation();
        this.chest.getWorld().playEffect(location, Effect.STEP_SOUND, Material.ENDER_CHEST);
        this.chest.getWorld().playEffect(location, Effect.TILE_BREAK, ENDER_CHEST_DATA);
        this.chest.setType(Material.AIR);
    }

    @EventHandler
    private void onClose(InventoryCloseEvent event) {
        if (this.viewers.remove(event.getPlayer())) {
            this.updateViewerCount();
        }
    }

    @EventHandler
    private void onBreak(BlockBreakEvent event) {
        if (!this.chest.equals(event.getBlock())) return;

        Player breaker = event.getPlayer();

        if (!this.owningTeam.isMember(breaker)) {
            for (Player member : this.owningTeam.getMembers()) {
                member.sendMessage(RewiBWPlugin.PREFIX + Message.TEAM_CHEST_DESTROY);
            }
        }

        this.destroy();
    }

    private void updateViewerCount() {
        PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(
                this.blockPosition,
                ENDER_CHEST_NMS,
                1,
                this.viewers.size()
        );

        for (Player player : this.chest.getWorld().getPlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

}
