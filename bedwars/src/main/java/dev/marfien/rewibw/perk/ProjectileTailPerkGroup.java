package dev.marfien.rewibw.perk;

import de.slikey.effectlib.util.ParticleEffect;
import dev.marfien.rewibw.RewiBWPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

public class ProjectileTailPerkGroup extends PerkGroup<ParticlePerk> {

    public ProjectileTailPerkGroup(String key, ItemStack displayItem, ParticlePerk defaultPerk, ParticlePerk... perks) {
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

            ParticlePerk perk = getOrDefault(player);
            if (perk != null) {
                this.effectTasks.put(projectile, new ProjectileParticleTask(projectile, perk).runTaskTimer(RewiBWPlugin.getInstance(), 0, 1));
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
        private final ParticlePerk perk;

        // Will be filled by the entity every tick
        // Used to avoid creating a new Location object every tick
        private final Location location = new Location(null, 0, 0, 0);

        @Override
        public void run() {
            if (!this.projectile.isValid()) {
                this.cancel();
                return;
            }

            this.projectile.getLocation(this.location);
            this.perk.spawnParticle(this.location);
        }
    }

}
