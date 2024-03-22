package dev.marfien.rewibw.perk;

import de.slikey.effectlib.util.ParticleEffect;
import dev.marfien.rewibw.shared.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PerkManager {

    public static final NoOpPerkGroup<DataPerk<Sound>> BED_DESTROY_SOUND_PERK_GROUP = new NoOpPerkGroup<>(
            ItemBuilder.of(Material.BED).setDisplayName("§6Bettabbau-Sound").asItemStack(),
            new DataPerk<>("Eisengolem-Tot", ItemBuilder.of(Material.IRON_BLOCK).setDisplayName("§9Eisengolem-Tod").asItemStack(), Sound.IRONGOLEM_DEATH),
            new DataPerk<>("Enderdrache-Grollen", ItemBuilder.of(Material.DRAGON_EGG).setDisplayName("§9Enderdrache-Grollen").asItemStack(), Sound.ENDERDRAGON_GROWL),
            new DataPerk<>("Wither-Spawn", ItemBuilder.of(Material.NETHER_STAR).setDisplayName("§9Wither-Spawn").asItemStack(), Sound.IRONGOLEM_DEATH),
            new DataPerk<>("Skelett-Tod", ItemBuilder.of(Material.BONE).setDisplayName("§9Skelett-Tod").asItemStack(), Sound.SKELETON_DEATH),
            new DataPerk<>("Wither-Tod", ItemBuilder.of(Material.SKULL_ITEM).setDamage((short) 1).setDisplayName("§9Wither-Tod").asItemStack(), Sound.WITHER_DEATH)
    );
    public static final NoOpPerkGroup<DataPerk<Sound>> KILL_SOUND_PERK_GROUP = new NoOpPerkGroup<>(
            ItemBuilder.of(Material.NOTE_BLOCK).setDisplayName("§6Kill-Sound").asItemStack(),
            null,
            new DataPerk<>("Kolben", ItemBuilder.of(Material.PISTON_BASE).setDisplayName("§eKolben").asItemStack(), Sound.PISTON_EXTEND),
            new DataPerk<>("Feuerwerk", ItemBuilder.of(Material.FIREWORK).setDisplayName("§eFeuerwerk").asItemStack(), Sound.FIREWORK_LAUNCH),
            new DataPerk<>("Ghast-Tod", ItemBuilder.of(Material.MONSTER_EGG).setDisplayName("§eGhast-Tod").asItemStack(), Sound.GHAST_DEATH),
            new DataPerk<>("Levelup", ItemBuilder.of(Material.EXP_BOTTLE).setDisplayName("§eLevelup").asItemStack(), Sound.LEVEL_UP),
            new DataPerk<>("Hund", ItemBuilder.of(Material.BONE).setDisplayName("§eHund").asItemStack(), Sound.WOLF_HOWL),
            new DataPerk<>("Skelett", ItemBuilder.of(Material.SKULL_ITEM).setDisplayName("§eSkelett").asItemStack(), Sound.SKELETON_HURT),
            new DataPerk<>("TNT", ItemBuilder.of(Material.TNT).setDisplayName("§eTNT").asItemStack(), Sound.EXPLODE),
            new DataPerk<>("Verwandlung", ItemBuilder.of(Material.GOLDEN_APPLE).setDisplayName("§eVerwandlung").asItemStack(), Sound.ZOMBIE_UNFECT)
    );

    private static final PerkGroup<?>[] PERK_GROUPS = new PerkGroup<?>[]{
            new ItemPerkGroup(
                    ItemBuilder.of(Material.STICK).setDisplayName("§6Knüppel").asItemStack(),
                    new ItemStackDataPerk("Knüppel", ItemBuilder.of(Material.STICK).setDisplayName("§cKnüppel").asItemStack()),
                    new ItemStackDataPerk("Knochen", ItemBuilder.of(Material.BONE).setDisplayName("§cKnochen").asItemStack()),
                    new ItemStackDataPerk("Blazerod", ItemBuilder.of(Material.BLAZE_ROD).setDisplayName("§cBlazerod").asItemStack()),
                    new ItemStackDataPerk("Feder", ItemBuilder.of(Material.FEATHER).setDisplayName("§cFeder").asItemStack())
            ),
            BED_DESTROY_SOUND_PERK_GROUP,
            KILL_SOUND_PERK_GROUP,
            new ProjectileTailPerkGroup(
                    ItemBuilder.of(Material.BOW).setDisplayName("§6Bogen-Particle").asItemStack(),
                    null,
                    new DataPerk<>("Herz", ItemBuilder.of(Material.RED_ROSE).setDisplayName("§dHerz").asItemStack(), ParticleEffect.HEART),
                    new DataPerk<>("Flammen", ItemBuilder.of(Material.BLAZE_POWDER).setDisplayName("§dFlammen").asItemStack(), ParticleEffect.FLAME),
                    new DataPerk<>("Noten", ItemBuilder.of(Material.NOTE_BLOCK).setDisplayName("§dNoten").asItemStack(), ParticleEffect.NOTE),
                    new DataPerk<>("Funken", ItemBuilder.of(Material.NETHER_STAR).setDisplayName("§dFunken").asItemStack(), ParticleEffect.FIREWORKS_SPARK),
                    new DataPerk<>("Slime", ItemBuilder.of(Material.SLIME_BALL).setDisplayName("§dSlime").asItemStack(), ParticleEffect.SLIME),
                    new DataPerk<>("Feuer", ItemBuilder.of(Material.LAVA_BUCKET).setDisplayName("§dFeuer").asItemStack(), ParticleEffect.LAVA),
                    new DataPerk<>("Bunter Staub", ItemBuilder.of(Material.GLOWSTONE).setDisplayName("§dBunter Staub").asItemStack(), ParticleEffect.REDSTONE)
            )
    };

    public static void init(Plugin plugin) {
        for (PerkGroup<?> perkGroup : PERK_GROUPS) {
            perkGroup.init(plugin);
        }
    }

    public static void openGui(Player player) {
        // TODO
    }
}
