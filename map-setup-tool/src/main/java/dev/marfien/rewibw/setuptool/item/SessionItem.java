package dev.marfien.rewibw.setuptool.item;

import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.CubeEffect;
import de.slikey.effectlib.util.ParticleEffect;
import dev.marfien.rewibw.setuptool.SetupSession;
import dev.marfien.rewibw.setuptool.SetupToolPlugin;
import dev.marfien.rewibw.shared.usable.ConsumeType;
import dev.marfien.rewibw.shared.usable.UsableItemInfo;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class SessionItem extends UsableItemInfo {

    protected static Location toCleanLocation(Location location) {
        return new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY(), location.getBlockZ() + 0.5);
    }

    protected static void addLocationEffect(Location location, ParticleEffect particleEffect, Color color, Material material) {
        Location blockLocation = location.getBlock().getLocation();
        CubeEffect effect = new CubeEffect(SetupToolPlugin.getEffectManager());
        effect.type = EffectType.REPEATING;
        effect.iterations = -1;
        effect.particle = particleEffect;
        effect.color = color;
        effect.material = material;
        effect.setLocation(blockLocation);
        effect.edgeLength = 1;
        effect.outlineOnly = true;
        SetupToolPlugin.effects.add(effect);
        effect.start();
    }

    protected SessionItem() {
        super(ConsumeType.NONE);
    }

    @Override
    protected boolean onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        SetupSession session = SetupToolPlugin.getSession(player);

        if (session == null) return false;

        if (!event.hasBlock()) return false;
        Block targetBlock = event.getClickedBlock().getRelative(event.getBlockFace());
        Location location = toCleanLocation(targetBlock.getLocation());

        this.onClick(event, player, session, location);
        return false;
    }

    protected abstract void onClick(PlayerInteractEvent event, Player player, SetupSession session, Location location);

}
