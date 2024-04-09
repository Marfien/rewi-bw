package dev.marfien.rewibw;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.HelixEffect;
import de.slikey.effectlib.util.ParticleEffect;
import dev.marfien.rewibw.shared.ItemBuilder;
import dev.marfien.rewibw.shared.Position;
import dev.marfien.rewibw.shop.ShopPrice;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;

@Getter
public enum ResourceType {

    BRONZE(ChatColor.RED, "Bronze", Material.CLAY_BRICK, null, 12),
    SILVER(ChatColor.GRAY, "Eisen", Material.IRON_INGOT, Color.SILVER, 10 * 20),
    GOLD(ChatColor.GOLD, "Gold", Material.GOLD_INGOT, Color.YELLOW, 30 * 20);

    private final ChatColor chatColor;
    private final String translation;
    private final Material material;
    private final Color effectColor;
    private final int spawnInterval;
    private final ItemStack itemStack;

    ResourceType(ChatColor chatColor, String translation, Material material, Color effectColor, int spawnInterval) {
        this.chatColor = chatColor;
        this.translation = translation;
        this.material = material;
        this.effectColor = effectColor;
        this.spawnInterval = spawnInterval;
        this.itemStack = ItemBuilder.of(this.material)
                .setDisplayName(this.getDisplayName())
                .asItemStack();
    }

    public ShopPrice withAmount(int amount) {
        return new ShopPrice(this, amount);
    }

    public String getDisplayName() {
        return this.chatColor + this.translation;
    }

    public BukkitTask startSpawning(World world, Position... positions) {
        Location[] locations = new Location[positions.length];
        for (int i = 0; i < positions.length; i++) {
            locations[i] = positions[i].toLocation(world);
        }

        return RewiBWPlugin.getScheduler().runTaskTimer(() -> {
            RewiBWPlugin.getPluginLogger().trace("Spawning {} at {} locations: {}", this::name, () -> locations.length, () -> Arrays.toString(locations));
            for (Location location : locations) {
                world.dropItem(location, this.itemStack)
                        .setVelocity(RewiBWPlugin.ZERO_VECTOR);
                if (this.effectColor == null) continue;
                Effect effect = createEffect(this.effectColor);
                effect.setLocation(location);
                effect.start();
            }
        }, this.spawnInterval * 2L, this.spawnInterval);
    }

    private static Effect createEffect(Color color) {
        EffectManager manager = RewiBWPlugin.getEffectManager();
        HelixEffect effect = new HelixEffect(manager);
        effect.color = color;
        effect.iterations = 1;
        effect.radius = 1;
        effect.type = EffectType.INSTANT;
        effect.particle = ParticleEffect.SPELL_MOB;
        effect.particles = 25;
        effect.asynchronous = true;
        return effect;
    }

}
