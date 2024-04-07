package dev.marfien.rewibw.setuptool.item;

import de.slikey.effectlib.util.ParticleEffect;
import dev.marfien.rewibw.shared.ItemBuilder;
import dev.marfien.rewibw.shared.Position;
import dev.marfien.rewibw.shared.config.MapConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpecSpawnSetter extends SessionItem {

    public static final ItemStack ITEM = ItemBuilder.of(Material.GLASS).setDisplayName("§aSpectator-Spawn setzen").asItemStack();

    @Override
    protected void onClick(PlayerInteractEvent event, Player player, MapConfig mapConfig, Location location) {
        location.setPitch(90);
        mapConfig.setSpectatorSpawn(new Position(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch()));
        addLocationEffect(location, ParticleEffect.FLAME, null, null);
        player.sendMessage("§aSpectator-Spawn set.");
    }
}
