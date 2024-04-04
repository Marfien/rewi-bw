package dev.marfien.rewibw.perk;

import de.slikey.effectlib.util.ParticleEffect;
import dev.marfien.rewibw.RewiBWPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ProjectileTailPerkGroup extends PerkGroup<DataPerk<ParticleEffect>> {

    @SafeVarargs
    public ProjectileTailPerkGroup(String key, ItemStack displayItem, DataPerk<ParticleEffect> defaultPerk, DataPerk<ParticleEffect>... perks) {
        super(key, displayItem, defaultPerk, perks);
    }

    @Override
    public void init(Plugin plugin, PerkGroup<?>[] perkGroups) {
        super.init(plugin, perkGroups);
        Bukkit.getPluginManager().registerEvents(new ProjectileParticlePerkListener(), plugin);
    }

    public class ProjectileParticlePerkListener implements Listener {

        private final Map<Projectile, BukkitTask> effectTasks = new HashMap<>();

        @EventHandler
        private void onBowShoot(ProjectileLaunchEvent event) {
            Projectile projectile = event.getEntity();
            if (!(projectile instanceof Arrow)) return;

            ProjectileSource shooter = projectile.getShooter();
            if (!(shooter instanceof Player)) return;
            Player player = (Player) shooter;

            DataPerk<ParticleEffect> data = getOrDefault(player);
            if (data == null) return;
            ParticleEffect effect = data.getData();

            if (effect == null) return;

            this.effectTasks.put(projectile, new ProjectileParticleTask(projectile, effect).runTaskTimer(RewiBWPlugin.getInstance(), 0, 1));
        }

        @EventHandler
        private void onHit(ProjectileHitEvent event) {
            BukkitTask task = this.effectTasks.remove(event.getEntity());
            if (task != null) {
                task.cancel();
            }
        }
    }

    private static class ProjectileParticleTask extends BukkitRunnable {

        private static final Random RANDOM = new Random();

        private final Projectile projectile;
        private final ParticleEffect effect;
        private final boolean isColorable;

        public ProjectileParticleTask(Projectile projectile, ParticleEffect effect) {
            this.projectile = projectile;
            this.effect = effect;
            this.isColorable = effect == ParticleEffect.REDSTONE || effect == ParticleEffect.SPELL_MOB || effect == ParticleEffect.SPELL_MOB_AMBIENT;
        }

        @Override
        public void run() {
            if (!this.projectile.isValid()) {
                this.cancel();
                return;
            }

            if (this.isColorable) {
                this.effect.display(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat(), 0, 1, this.projectile.getLocation(), 64);
            } else {
                this.effect.display(0, 0, 0, 0, 1, this.projectile.getLocation(), 64);
            }

        }
    }

}
