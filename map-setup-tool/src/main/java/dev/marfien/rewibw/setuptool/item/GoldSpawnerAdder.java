package dev.marfien.rewibw.setuptool.item;

import de.slikey.effectlib.util.ParticleEffect;
import dev.marfien.rewibw.setuptool.SetupSession;
import dev.marfien.rewibw.shared.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class GoldSpawnerAdder extends SessionItem {

    public static final ItemStack ITEM = ItemBuilder.of(Material.GOLD_BLOCK).setDisplayName("§aGold-Spawner hinzufügen").asItemStack();

    @Override
    protected void onClick(PlayerInteractEvent event, Player player, SetupSession session, Location location) {
        session.getGoldSpawns().add(location);
        addLocationEffect(location, ParticleEffect.BLOCK_CRACK, null, Material.GOLD_BLOCK);
        player.sendMessage("§aGold-Spawner added.");
    }

}