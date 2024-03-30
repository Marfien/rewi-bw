package dev.marfien.rewibw.shop;

import com.mysql.jdbc.ServerPreparedStatement;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.shared.InventoryUtil;
import dev.marfien.rewibw.shared.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public class ShopItem implements Shoppable {

    private final ShopPrice price;

    private final Function<Player, ItemStack> displayItem;

    private final Function<Player, ItemStack> itemStack;

    private final int shiftClickMultiplier;

    public ShopItem(ShopPrice price, ItemStack itemStack, int shiftClickMultiplier) {
        this(price, itemStack, itemStack, shiftClickMultiplier);
    }

    public ShopItem(ShopPrice price, ItemStack displayItem, ItemStack itemStack, int shiftClickMultiplier) {
        this(price, ignored -> displayItem.clone(), ignored -> itemStack.clone(), shiftClickMultiplier);
    }

    public ShopItem(ShopPrice price, Function<Player, ItemStack> itemStack, int shiftClickMultiplier) {
        this(price, itemStack, itemStack, shiftClickMultiplier);
    }

    @Override
    public ItemStack getDisplayItemFor(Player player) {
        ItemStack item = this.displayItem.apply(player);

        if (item == null) return null;
        if (!item.hasItemMeta()) return item;

        return ItemBuilder.of(this.displayItem.apply(player))
                .addLoreLine("")
                .addLoreLine(" " + this.price.toColoredString())
                .asItemStack();
    }

    @Override
    public boolean giveToPlayer(Player player, ItemStack[] contents, int multiplier, int slot) {
        if (slot != -1) {
            return moveToSlot(player, contents, slot);
        }

        ItemStack item = this.itemStack.apply(player);
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

        for (int i = 0; i < contents.length; i++) {
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
        ItemStack item = this.itemStack.apply(player);

        ItemStack inSlot = contents[slot];
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

}
