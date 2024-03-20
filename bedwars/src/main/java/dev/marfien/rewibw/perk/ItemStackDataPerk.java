package dev.marfien.rewibw.perk;

import org.bukkit.inventory.ItemStack;

public class ItemStackDataPerk extends DataPerk<ItemStack> {

    public ItemStackDataPerk(String name, ItemStack itemStack) {
        super(name, itemStack, itemStack);
    }

}
