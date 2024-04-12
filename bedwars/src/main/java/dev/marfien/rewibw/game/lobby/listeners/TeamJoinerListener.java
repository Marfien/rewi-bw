package dev.marfien.rewibw.game.lobby.listeners;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.shared.TeamColor;
import dev.marfien.rewibw.team.GameTeam;
import dev.marfien.rewibw.team.TeamManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamJoinerListener implements Listener {

    private final Map<UUID, GameTeam> joiners = new HashMap<>();

    public void addJoiner(Location location, GameTeam team) {
        TeamColor color = team.getColor();

        ArmorStand armorStand = location.getChunk().getWorld().spawn(location, ArmorStand.class);
        armorStand.setCustomName(color.getChatColor() + "Team " + color.getName());
        armorStand.setCustomNameVisible(true);
        armorStand.setBoots(team.getBoots());
        armorStand.setLeggings(team.getLeggings());
        armorStand.setChestplate(team.getChestplate());
        armorStand.setHelmet(null);
        armorStand.setGravity(false);
        armorStand.setBasePlate(false);
        armorStand.setArms(false);

        RewiBWPlugin.getPluginLogger().info("Added team joiner for team {} (entityId: {}, uuid: {}, location: {})", color, armorStand.getEntityId(), armorStand.getUniqueId(), armorStand.getLocation());
        this.joiners.put(armorStand.getUniqueId(), team);
    }

    public void clearJoiners() {
        this.joiners.clear();
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        Entity rightClicked = event.getRightClicked();
        if (!(rightClicked instanceof ArmorStand)) return;

        GameTeam team = this.joiners.get(rightClicked.getUniqueId());
        if (team == null) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        if (TeamManager.getTeam(player) == team) {
            player.sendMessage(RewiBWPlugin.PREFIX + Message.ALREADY_IN_TEAM);
            return;
        }

        if (TeamManager.tryJoinTeam(player, team)) {
            player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 0.7F, 1.0F);
            player.sendMessage(RewiBWPlugin.PREFIX + Message.TEAM_JOINED.format(team.getColor().getName()));
        } else {
            player.sendMessage(RewiBWPlugin.PREFIX + Message.TEAM_FULL);
        }
    }

    @EventHandler
    private void onEntityDamage(EntityDamageEvent event) {
        if (this.joiners.containsKey(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

}
