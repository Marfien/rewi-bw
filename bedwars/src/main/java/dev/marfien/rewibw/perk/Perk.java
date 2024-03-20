package dev.marfien.rewibw.perk;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public abstract class Perk {

    private final String name;
    private final ItemStack displayItem;

}
