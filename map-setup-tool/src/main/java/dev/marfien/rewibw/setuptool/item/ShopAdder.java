package dev.marfien.rewibw.setuptool.item;

import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.LineEffect;
import de.slikey.effectlib.util.ParticleEffect;
import dev.marfien.rewibw.setuptool.SetupSession;
import dev.marfien.rewibw.setuptool.SetupToolPlugin;
import dev.marfien.rewibw.shared.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ShopAdder extends SessionItem {

    public static final ItemStack ITEM = ItemBuilder.of(Material.ARMOR_STAND).setDisplayName("§aShop hinzufügen").asItemStack();

    @Override
    protected void onClick(PlayerInteractEvent event, Player player, SetupSession session, Location location) {
        location.setDirection(location.clone().subtract(player.getLocation()).toVector());
        location.setPitch(0);
        location.setYaw(Math.round(location.getYaw() / 45) * 45F);

        session.getShops().add(location);
        addLocationEffect(location, ParticleEffect.VILLAGER_HAPPY, null, null);
        player.sendMessage("§aShop added.");

        LineEffect effect = new LineEffect(SetupToolPlugin.getEffectManager());
        effect.setLocation(location);
        effect.particle = ParticleEffect.VILLAGER_HAPPY;
        effect.length = 1;
        effect.type = EffectType.REPEATING;
        effect.iterations = -1;
    }
}