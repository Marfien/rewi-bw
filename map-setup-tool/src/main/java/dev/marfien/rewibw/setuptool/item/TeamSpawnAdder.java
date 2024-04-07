package dev.marfien.rewibw.setuptool.item;

import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.LineEffect;
import de.slikey.effectlib.util.ParticleEffect;
import dev.marfien.rewibw.setuptool.SetupToolPlugin;
import dev.marfien.rewibw.shared.ItemBuilder;
import dev.marfien.rewibw.shared.Position;
import dev.marfien.rewibw.shared.TeamColor;
import dev.marfien.rewibw.shared.config.MapConfig;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class TeamSpawnAdder extends SessionItem {

    private final TeamColor team;

    @Override
    protected void onClick(PlayerInteractEvent event, Player player, MapConfig mapConfig, Location location) {
        location.setDirection(player.getLocation().subtract(location).toVector());
        location.setPitch(0);
        location.setYaw(Math.round(location.getYaw() / 45) * 45F);

        mapConfig.getTeams().get(team).setSpawn(new Position(
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        ));

        addLocationEffect(location, ParticleEffect.REDSTONE, this.team.getDyeColor().getColor(), null);
        player.sendMessage("§aSpawn for team " + team.getDisplayName() + " set.");
        player.getInventory().setItem(7, TeamBedSetter.getItemFor(this.team));

        LineEffect effect = new LineEffect(SetupToolPlugin.getEffectManager());
        effect.setLocation(location.add(0, 0.5, 0));
        effect.particle = ParticleEffect.REDSTONE;
        effect.color = this.team.getDyeColor().getColor();
        effect.length = 1;
        effect.type = EffectType.REPEATING;
        effect.iterations = -1;
        effect.period = 20;
        effect.start();
        SetupToolPlugin.effects.add(effect);
    }

    public static ItemStack getItemFor(TeamColor color) {
        return ItemBuilder.of(Material.BANNER)
                .setBannerBaseColor(color.getDyeColor())
                .setDisplayName(color.getChatColor() + "Set spawn")
                .asItemStack();
    }
}
