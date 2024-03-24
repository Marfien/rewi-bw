package dev.marfien.rewibw.perk;

import dev.marfien.rewibw.shared.ItemBuilder;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public abstract class Perk {

    private final String name;
    private final ItemStack displayItem;

    public Perk(String name, ItemStack displayItem) {
        this.name = name;
        this.displayItem = ItemBuilder.of(displayItem)
                .setLore(" ", "§e§lGekauft")
                .asItemStack();
    }

}
