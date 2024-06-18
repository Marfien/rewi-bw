package dev.marfien.rewibw.shop;

import dev.marfien.rewibw.shared.InventoryUtil;
import dev.marfien.rewibw.shared.ItemBuilder;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public abstract class ShopItem extends Shoppable {

    private final int shiftClickMultiplier;

    protected ShopItem(ShopPrice price, int shiftClickMultiplier) {
        super(price);
        this.shiftClickMultiplier = shiftClickMultiplier;
    }

    public abstract ItemStack createItem(Player player);

    @Override
    public boolean giveToPlayer(Player player, ItemStack[] contents, int multiplier, int slot) {
        if (slot != -1) {
            return moveToSlot(player, contents, slot);
        }

        ItemStack item = this.createItem(player);
        int maxStackSize = item.getMaxStackSize();
        int totalAmount = item.getAmount() * multiplier;

        for (ItemStack current : contents) {
            if (!item.isSimilar(current)) continue;

            int currentAmount = current.getAmount();

            if (currentAmount + totalAmount <= maxStackSize) {
                current.setAmount(currentAmount + totalAmount);
                return true;
            }

            totalAmount -= maxStackSize - currentAmount;
            current.setAmount(maxStackSize);
        }

        for (int i = 0; i < contents.length && totalAmount > 0; i++) {
            if (contents[i] != null) continue;

            int amount = Math.min(totalAmount, maxStackSize);

            ItemStack clone = item.clone();
            clone.setAmount(amount);
            contents[i] = clone;
            totalAmount -= amount;
        }

        return totalAmount <= 0;
    }

    private boolean moveToSlot(Player player, ItemStack[] contents, int slot) {
        ItemStack item = this.createItem(player);

        ItemStack inSlot = contents[slot];
        if (inSlot == null) {
            contents[slot] = item;
            return true;
        }

        if (!item.isSimilar(inSlot)) {
            int firstEmpty = InventoryUtil.firstEmptySlot(contents);
            if (firstEmpty == -1) {
                return false;
            }

            ItemStack old = contents[slot];
            contents[slot] = item;
            contents[firstEmpty] = old;
            return true;
        }

        int maxStackSize = item.getMaxStackSize();
        int totalAmount = item.getAmount() + inSlot.getAmount();
        if (totalAmount <= maxStackSize) {
            inSlot.setAmount(totalAmount);
            return true;
        }

        int firstEmpty = InventoryUtil.firstEmptySlot(contents);
        if (firstEmpty == -1) {
            return false;
        }

        inSlot.setAmount(maxStackSize);

        item.setAmount(totalAmount - maxStackSize);
        contents[firstEmpty] = item;
        return true;
    }

    void addPriceToLore(ItemStack itemStack) {
        ItemBuilder.of(itemStack)
                .addLoreLines(" ", " " + super.getPrice().toColoredString());
    }

}
