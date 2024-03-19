package dev.marfien.rewibw.shared.usable;

import org.bukkit.inventory.ItemStack;

public enum ConsumeType {

    SHRINK_DURABILITY {
        @Override
        public ItemStack consumeItem(ItemStack itemStack) {
            short newDamage = (short) (itemStack.getDurability() + 1);
            if (newDamage >= itemStack.getType().getMaxDurability()) return null;
            itemStack.setDurability((short) (itemStack.getDurability() + 1));
            return itemStack;
        }
    },
    DECREASE_AMOUNT {
        @Override
        public ItemStack consumeItem(ItemStack itemStack) {
            int newAmount = itemStack.getAmount() - 1;
            if (newAmount <= 0) return null;

            itemStack.setAmount(itemStack.getAmount() - 1);
            return itemStack;
        }
    },
    NONE;

    public ItemStack consumeItem(ItemStack itemStack) {
        return itemStack;
    }

}
