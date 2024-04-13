package dev.marfien.rewibw.perk;

import de.slikey.effectlib.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;

public abstract class ParticlePerk extends Perk {

    private static final Random RANDOM = new Random();

    protected ParticlePerk(String name, ItemStack displayItem) {
        super(name, displayItem);
    }

    public abstract void spawnParticle(Location location);

    public static class StaticParticlePerk extends ParticlePerk {

        private final ParticleEffect effect;

        public StaticParticlePerk(String name, ItemStack displayItem, ParticleEffect effect) {
            super(name, displayItem);
            this.effect = effect;
        }

        @Override
        public void spawnParticle(Location location) {
            this.effect.display(0, 0, 0, 0, 1, location, 64);
        }
    }

    public static class ColoredParticlePerk extends ParticlePerk {

        private final ParticleEffect effect;
        private final Vector color;

        public ColoredParticlePerk(String name, ItemStack displayItem, ParticleEffect effect) {
            this(name, displayItem, effect, null);
        }

        public ColoredParticlePerk(String name, ItemStack displayItem, ParticleEffect effect, Vector color) {
            super(name, displayItem);
            this.effect = effect;
            this.color = color;
        }

        @Override
        public void spawnParticle(Location location) {
            if (this.color != null) {
                this.effect.display(
                        (float) this.color.getX(),
                        (float) this.color.getY(),
                        (float) this.color.getZ(),
                        0,
                        0,
                        location,
                        64
                );
                return;
            }
            this.effect.display(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat(), 1, 0, location, 64);
        }
    }

    public static class NoteParticlePerk extends ParticlePerk {

        public NoteParticlePerk(String name, ItemStack displayItem) {
            super(name, displayItem);
        }

        @Override
        public void spawnParticle(Location location) {
            ParticleEffect.NOTE.display(RANDOM.nextFloat(), 0, 0, 1, 0, location, 64);
        }
    }

}
