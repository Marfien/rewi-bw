package dev.marfien.rewibw.team;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.shared.TeamColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class TeamJoiner implements Listener {

    private final ArmorStand armorStand;
    private final GameTeam team;

    public TeamJoiner(Location location, GameTeam team) {
        this.team = team;
        TeamColor color = team.getColor();
        location.getChunk();
        this.armorStand = location.getWorld().spawn(location, ArmorStand.class);
        this.armorStand.setCustomName(color.getChatColor() + "Team " + color.getName());
        this.armorStand.setCustomNameVisible(true);
        this.armorStand.setBoots(this.team.getBoots());
        this.armorStand.setLeggings(this.team.getLeggings());
        this.armorStand.setChestplate(this.team.getChestplate());
        this.armorStand.setHelmet(null);
        this.armorStand.setGravity(false);
        this.armorStand.setBasePlate(false);
        this.armorStand.setArms(false);
        for (Entity nearbyEntity : this.armorStand.getNearbyEntities(0.1, 0.1, 0.1)) {
            if (nearbyEntity == this.armorStand) continue;
            nearbyEntity.remove();
        }
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        if (!event.getRightClicked().equals(this.armorStand)) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        if (TeamManager.getTeam(player) == this.team) {
            player.sendMessage(RewiBWPlugin.PREFIX + Message.ALREADY_IN_TEAM);
            return;
        }

        if (TeamManager.tryJoinTeam(player, this.team)) {
            player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 0.7F, 1.0F);
            player.sendMessage(RewiBWPlugin.PREFIX + Message.TEAM_JOINED.format(this.team.getColor().getName()));
        } else {
            player.sendMessage(RewiBWPlugin.PREFIX + Message.TEAM_FULL);
        }
    }

    @EventHandler
    private void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity().equals(this.armorStand)) {
            event.setCancelled(true);
        }
    }

    public void onUnload(WorldUnloadEvent event) {
        if (!event.getWorld().equals(this.armorStand.getWorld())) return;

        this.armorStand.remove();
    }

}
