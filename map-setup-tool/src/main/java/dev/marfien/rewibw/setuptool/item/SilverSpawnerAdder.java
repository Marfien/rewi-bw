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

public class SilverSpawnerAdder extends SessionItem {

    public static final ItemStack ITEM = ItemBuilder.of(Material.IRON_BLOCK).setDisplayName("§aEisen-Spawner hinzufügen").asItemStack();

    @Override
    protected void onClick(PlayerInteractEvent event, Player player, MapConfig session, Location location) {
        MapConfig.SpawnerConfig spawner = session.getSpawner();
        spawner.setBronze(appendToArray(
                spawner.getBronze(),
                new Position(
                        location.getX(),
                        location.getY(),
                        location.getZ(),
                        location.getYaw(),
                        location.getPitch()
                ))
        );

        addLocationEffect(location, ParticleEffect.BLOCK_DUST, null, Material.IRON_BLOCK);
        player.sendMessage("§aSilver-Spawner added.");
    }

}
