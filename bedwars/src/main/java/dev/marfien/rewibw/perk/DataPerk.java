package dev.marfien.rewibw.perk;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class DataPerk<T> extends Perk {

    private final T data;

    public DataPerk(String name, ItemStack displayItem, T data) {
        super(name, displayItem);
        this.data = data;
    }
}
