package dev.marfien.rewibw.usable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public class ItemStackHashWrapper {

    private final ItemStack itemStack;

    @Override
    public int hashCode() {
        return hash(this.itemStack);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ItemStackHashWrapper)) return false;
        ItemStackHashWrapper other = (ItemStackHashWrapper) obj;
        if (other.itemStack == this.itemStack) return true;
        if (this.itemStack == null) return false;

        return this.itemStack.isSimilar(other.itemStack);
    }

    public static int hash(ItemStack itemStack) {
        // Basically a clone of ItemStack#hashCode() but without accounting the amount
        int hash = 1;
        hash = hash * 31 + itemStack.getTypeId();
        hash = hash * 31 + (itemStack.getDurability() & '\uffff');
        hash = hash * 31 + (itemStack.hasItemMeta() ? itemStack.getItemMeta().hashCode() : 0);
        return hash;
    }

}
