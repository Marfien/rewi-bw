package dev.marfien.rewibw.setuptool.item;

import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.LineEffect;
import de.slikey.effectlib.util.ParticleEffect;
import dev.marfien.rewibw.setuptool.SetupToolPlugin;
import dev.marfien.rewibw.shared.ItemBuilder;
import dev.marfien.rewibw.shared.Position;
import dev.marfien.rewibw.shared.config.MapConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ShopAdder extends SessionItem {

    public static final ItemStack ITEM = ItemBuilder.of(Material.ARMOR_STAND).setDisplayName("§aShop hinzufügen").asItemStack();

    @Override
    protected void onClick(PlayerInteractEvent event, Player player, MapConfig mapConfig, Location location) {
        location.setDirection(player.getLocation().subtract(location).toVector());
        location.setPitch(0);
        location.setYaw(Math.round(location.getYaw() / 45) * 45F);

        mapConfig.setShops(appendToArray(
                mapConfig.getShops(),
                new Position(
                        location.getX(),
                        location.getY(),
                        location.getZ(),
                        location.getYaw(),
                        location.getPitch()
                ))
        );

        addLocationEffect(location, ParticleEffect.VILLAGER_HAPPY, null, null);
        player.sendMessage("§aShop added.");

        LineEffect effect = new LineEffect(SetupToolPlugin.getEffectManager());
        effect.setLocation(location.add(0, 0.5, 0));
        effect.particle = ParticleEffect.VILLAGER_HAPPY;
        effect.length = 1;
        effect.type = EffectType.REPEATING;
        effect.iterations = -1;
        effect.period = 40;
        effect.start();
        SetupToolPlugin.getEffects().add(effect);
    }
}
