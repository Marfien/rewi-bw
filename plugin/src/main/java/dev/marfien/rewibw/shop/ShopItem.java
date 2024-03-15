package dev.marfien.rewibw.shop;

import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.util.ItemBuilder;
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
    public void giveToPlayer(Player player, int multiplier, int slot) {
        Inventory inventory = player.getInventory();

        ItemStack item = this.itemStack.apply(player);
        int maxStackSize = item.getMaxStackSize();
        int totalAmount = item.getAmount() * multiplier;
        int fullStacks = totalAmount / maxStackSize;
        int rest = totalAmount % maxStackSize;

        item.setAmount(maxStackSize);
        ItemStack[] stacks = new ItemStack[fullStacks + (rest == 0 ? 0 : 1)];

        if (stacks.length == 0) return;

        Arrays.fill(stacks, item.clone());

        if (rest != 0) {
            item.setAmount(rest);
            stacks[stacks.length - 1] = item;
        }


        if (slot != -1) {
            ItemStack inSlot = inventory.getItem(slot);
            if (item.isSimilar(inSlot)) {
                int restPlusExisting = inSlot.getAmount() + rest;

                if (restPlusExisting <= item.getMaxStackSize()) {
                    inSlot.setAmount(restPlusExisting);
                    // The last one is not needed anymore
                    stacks = Arrays.copyOf(stacks, stacks.length - 1);
                } else {
                    int dif = restPlusExisting - item.getMaxStackSize();
                    inSlot.setAmount(item.getMaxStackSize());
                    stacks[stacks.length - 1].setAmount(dif);
                }

            } else {
                inventory.setItem(slot, stacks[0]);
                if (inSlot == null) {
                    stacks = Arrays.copyOfRange(stacks, 1, stacks.length);
                } else {
                    stacks[0] = inSlot;
                }
            }
        }

        Map<Integer, ItemStack> overflow = inventory.addItem(stacks);
        // Drop all items that don"t fit in
        Location location = player.getLocation().add(0, 0.5, 0);
        for (ItemStack value : overflow.values()) {
            Item droppedItem = player.getWorld().dropItem(location, value);
            droppedItem.setVelocity(RewiBWPlugin.ZERO_VECTOR);
        }
    }
}
