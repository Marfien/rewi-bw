package dev.marfien.rewibw.setuptool.item;

import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.LineEffect;
import de.slikey.effectlib.util.ParticleEffect;
import dev.marfien.rewibw.setuptool.SetupToolPlugin;
import dev.marfien.rewibw.shared.ItemBuilder;
import dev.marfien.rewibw.shared.TeamColor;
import dev.marfien.rewibw.shared.config.MapConfig;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Bed;

@RequiredArgsConstructor
public class TeamBedSetter extends SessionItem {

    private final TeamColor team;

    @Override
    protected void onClick(PlayerInteractEvent event, Player player, MapConfig mapConfig, Location location) {
        Block block = event.getClickedBlock();

        if (block.getType() != Material.BED_BLOCK) {
            player.sendMessage("§cYou must click on a bed.");
            return;
        }

        Location bedLocation = block.getLocation();
        MapConfig.MapTeamConfig info = mapConfig.getTeams().get(this.team);
        MapConfig.TeamBedConfig teamBed = new MapConfig.TeamBedConfig();
        teamBed.setX(bedLocation.getBlockX());
        teamBed.setY(bedLocation.getBlockY());
        teamBed.setZ(bedLocation.getBlockZ());

        Bed bed = (Bed) block.getState().getData();
        BlockFace direction = bed.isHeadOfBed() ? bed.getFacing() : bed.getFacing().getOppositeFace();
        teamBed.setDirection(direction);

        info.setBed(teamBed);
        player.sendMessage("§aBed set for " + this.team.getDisplayName() + "§a.");
        player.getInventory().setItem(7, TeamAdder.ITEM);

        addLocationEffect(location, ParticleEffect.REDSTONE, this.team.getDyeColor().getColor(), null);

        LineEffect effect = new LineEffect(SetupToolPlugin.getEffectManager());
        effect.setLocation(toCleanLocation(block.getLocation()));
        effect.setTargetLocation(effect.getLocation().clone().add(direction.getModX(), 1 + direction.getModY(), direction.getModZ()));
        effect.particle = ParticleEffect.REDSTONE;
        effect.color = this.team.getDyeColor().getColor();
        effect.length = 1;
        effect.type = EffectType.REPEATING;
        effect.iterations = -1;
    }

    public static ItemStack getItemFor(TeamColor teamColor) {
        return ItemBuilder.of(Material.NETHER_STAR)
                .setDisplayName(teamColor.getChatColor() + "Set Bed")
                .asItemStack();
    }
}
