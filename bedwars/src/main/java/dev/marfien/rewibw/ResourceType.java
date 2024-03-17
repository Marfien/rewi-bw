package dev.marfien.rewibw;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.HelixEffect;
import de.slikey.effectlib.util.ParticleEffect;
import dev.marfien.rewibw.shop.ShopPrice;
import dev.marfien.rewibw.util.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;

@Getter
@AllArgsConstructor
public enum ResourceType {

    BRONZE(ChatColor.RED, "Bronze", Material.CLAY_BRICK, null, 10),
    SILVER(ChatColor.GRAY, "Eisen", Material.IRON_INGOT, Color.SILVER, 10 * 20),
    GOLD(ChatColor.GOLD, "Gold", Material.GOLD_INGOT, Color.YELLOW, 30 * 20);

    private final ChatColor chatColor;
    private final String translation;
    private final Material material;
    private final Color effectColor;
    private final int spawnInterval;

    public ShopPrice withAmount(int amount) {
        return new ShopPrice(this, amount);
    }

    public String getDisplayName() {
        return this.chatColor + this.translation;
    }

    public BukkitTask startSpawning(Collection<Location> locations) {
        ItemStack item = ItemBuilder.of(this.material)
                .setDisplayName(this.getDisplayName())
                .asItemStack();

        return Bukkit.getScheduler().runTaskTimer(RewiBWPlugin.getInstance(), () -> {
            for (Location location : locations) {
                location.getWorld()
                        .dropItem(location, item.clone())
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
