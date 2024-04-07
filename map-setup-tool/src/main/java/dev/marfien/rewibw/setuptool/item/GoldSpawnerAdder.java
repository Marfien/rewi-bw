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

public class GoldSpawnerAdder extends SessionItem {

    public static final ItemStack ITEM = ItemBuilder.of(Material.GOLD_BLOCK).setDisplayName("§aGold-Spawner hinzufügen").asItemStack();

    @Override
    protected void onClick(PlayerInteractEvent event, Player player, MapConfig mapConfig, Location location) {
        MapConfig.SpawnerConfig spawner = mapConfig.getSpawner();
        spawner.setGold(appendToArray(
                spawner.getGold(),
                new Position(
                        location.getX(),
                        location.getY(),
                        location.getZ(),
                        location.getYaw(),
                        location.getPitch()
                ))
        );

        addLocationEffect(location, ParticleEffect.BLOCK_DUST, null, Material.GOLD_BLOCK);
        player.sendMessage("§aGold-Spawner added.");
    }

}
