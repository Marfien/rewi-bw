package dev.marfien.rewibw.setuptool.item;

import de.slikey.effectlib.util.ParticleEffect;
import dev.marfien.rewibw.setuptool.SetupSession;
import dev.marfien.rewibw.shared.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpecSpawnSetter extends SessionItem {

    public static final ItemStack ITEM = ItemBuilder.of(Material.GLASS).setDisplayName("§aSpectator-Spawn setzen").asItemStack();

    @Override
    protected void onClick(PlayerInteractEvent event, Player player, SetupSession session, Location location) {
        location.setPitch(90);
        session.setSpawn(location);
        addLocationEffect(location, ParticleEffect.FLAME, null, null);
        player.sendMessage("§aSpectator-Spawn set.");
    }
}
