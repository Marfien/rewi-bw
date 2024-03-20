package dev.marfien.rewibw.perk;

import de.slikey.effectlib.util.ParticleEffect;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class PerkManager {

    @Getter private static final Perk<Sound> bedDestroySoundPerk = new NoOpPerk<>(Sound.IRONGOLEM_DEATH);
    @Getter private static final Perk<Sound> killSoundPerk = new NoOpPerk<>(null);
    @Getter private static final Perk<Material> stickPerk = new MaterialPerk(Material.STICK);
    @Getter private static final Perk<ParticleEffect> bowParticlesPerk = new ProjectileParticlePerk(null);
    @Getter private static final Perk<Sound> gameEndMelodyPerk = new NoOpPerk<>(Sound.IRONGOLEM_DEATH);

    public static void init(Plugin plugin) {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(bedDestroySoundPerk.getListener(), plugin);
        pluginManager.registerEvents(killSoundPerk.getListener(), plugin);
        pluginManager.registerEvents(stickPerk.getListener(), plugin);
        pluginManager.registerEvents(bowParticlesPerk.getListener(), plugin);
        pluginManager.registerEvents(gameEndMelodyPerk.getListener(), plugin);
    }

}
