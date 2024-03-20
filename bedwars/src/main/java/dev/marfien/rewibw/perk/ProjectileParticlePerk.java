package dev.marfien.rewibw.perk;

import de.slikey.effectlib.util.ParticleEffect;
import dev.marfien.rewibw.RewiBWPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class ProjectileParticlePerk extends AbstractPerk<ParticleEffect> {

    public ProjectileParticlePerk(ParticleEffect defaultPerk) {
        super(defaultPerk, ProjectileParticlePerkListener.class);
    }

    public class ProjectileParticlePerkListener implements Listener {

        private final Map<Projectile, BukkitTask> effectTasks = new HashMap<>();

        @EventHandler
        private void onBowShoot(ProjectileLaunchEvent event) {
            ProjectileSource shooter = event.getEntity().getShooter();
            if (!(shooter instanceof Player)) return;
            Player player = (Player) shooter;

            ParticleEffect effect = getOrDefault(player);

            if (effect != null) {
                Projectile projectile = event.getEntity();
                this.effectTasks.put(projectile, new ProjectileParticleTask(projectile, effect).runTaskTimer(RewiBWPlugin.getInstance(), 0, 2));
            }
        }

        @EventHandler
        private void onHit(ProjectileHitEvent event) {
            BukkitTask task = this.effectTasks.remove(event.getEntity());
            if (task != null) {
                task.cancel();
            }
        }
    }

    @RequiredArgsConstructor
    private static class ProjectileParticleTask extends BukkitRunnable {

        private final Projectile projectile;
        private final ParticleEffect effect;

        @Override
        public void run() {
            this.effect.display(0, 0, 0, 0, 1, this.projectile.getLocation(), 64);
        }
    }

}
