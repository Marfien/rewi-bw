package dev.marfien.rewibw.perk;

import dev.marfien.rewibw.shared.ItemBuilder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Consumer;

@ToString
@EqualsAndHashCode(callSuper = true)
public class ItemStackTransformPerk extends Perk {


    private final Material material;
    private final Short damage;
    private final Consumer<ItemMeta> transformer;

    public ItemStackTransformPerk(String name, Material material, short damage, Consumer<ItemMeta> transformer) {
        super(name, createDisplayItem(material, damage, name, transformer));
        this.material = material;
        this.damage = damage;
        this.transformer = transformer;
    }

    public ItemStackTransformPerk(String name, Material material, short damage) {
        this(name, material, damage, ignored -> {});
    }

    public ItemStackTransformPerk(String name, Material material, Consumer<ItemMeta> transformer) {
        super(name, createDisplayItem(material, null, name, transformer));
        this.material = material;
        this.damage = null;
        this.transformer = transformer;
    }

    public ItemStackTransformPerk(String name, Material material) {
        this(name, material, ignored -> {});
    }

    public void transformItem(ItemStack item) {
        item.setType(this.material);
        if (this.damage != null) {
            item.setDurability(this.damage);
        }
        ItemMeta meta = item.getItemMeta();
        this.transformer.accept(meta);
        item.setItemMeta(meta);
    }

    public boolean isSimilar(ItemStack item) {
        if (item == null) return false;
        if (item.getType() != this.material) return false;

        return this.damage == null || item.getDurability() == this.damage;
    }

    private static ItemStack createDisplayItem(Material material, Short damage, String name, Consumer<ItemMeta> transformer) {
        ItemStack stack = ItemBuilder.of(material)
                .setDisplayName(name)
                .asItemStack();
        if (damage != null) {
            stack.setDurability(damage);
        }

        ItemMeta meta = stack.getItemMeta();
        transformer.accept(meta);
        stack.setItemMeta(meta);
        return stack;
    }

}
