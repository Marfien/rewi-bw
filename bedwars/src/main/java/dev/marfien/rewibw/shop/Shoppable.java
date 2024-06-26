package dev.marfien.rewibw.shop;

import dev.marfien.rewibw.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class Shoppable implements ShopButton {

    public static final Shoppable SPACER = new Shoppable(null) {
        @Override
        public ShopPrice getPrice() {
            return null;
        }

        @Override
        public boolean giveToPlayer(Player player, ItemStack[] contents, int multiplier, int slot) {
            return true;
        }

        @Override
        public int getShiftClickMultiplier() {
            return 0;
        }

        @Override
        public ItemStack getDisplayItemFor(Player player) {
            return null;
        }

        @Override
        public void onClick(InventoryClickEvent event) {
            // Do nothing
        }
    };

    private final ShopPrice price;

    public abstract boolean giveToPlayer(Player player, ItemStack[] contents, int multiplier, int slot);

    public abstract int getShiftClickMultiplier();

    @Override
    public void onClick(InventoryClickEvent event) {
        Player clicker = (Player) event.getWhoClicked();
        Inventory playerInventory = clicker.getInventory();

        ItemStack[] inventoryContents = playerInventory.getContents().clone();

        List<int[]> slotsWithAmount = this.getSortedSlotAmountMap(inventoryContents);
        int multiplier = this.getMultiplierAndRemoveResources(
                inventoryContents,
                slotsWithAmount,
                event.isShiftClick() ? this.getShiftClickMultiplier() : 1
        );

        if (multiplier < 0) {
            clicker.sendMessage("§b[Shop] " + Message.SHOP_NOT_ENOUGH_RESOURCES.format(price.getType().getTranslation()));
            clicker.playSound(clicker.getLocation(), Sound.NOTE_BASS_GUITAR, 1, 1);
            return;
        }

        if (!this.giveToPlayer(clicker, inventoryContents, multiplier, event.getHotbarButton())) {
            clicker.sendMessage("§b[Shop] " + Message.SHOP_NOT_ENOUGH_SPACE);
            clicker.playSound(clicker.getLocation(), Sound.NOTE_BASS_GUITAR, 1, 1);
            return;
        }

        playerInventory.setContents(inventoryContents);
        clicker.playSound(clicker.getLocation(), Sound.ITEM_PICKUP, 1, 1);
    }

    private List<int[]> getSortedSlotAmountMap(ItemStack[] inventoryContents) {
        List<int[]> slots = new ArrayList<>(inventoryContents.length / 2);

        // Collect all the slots with matching resource type
        for (int i = 0; i < inventoryContents.length; i++) {
            ItemStack current = inventoryContents[i];
            if (current != null && this.price.getType().getMaterial() == current.getType()) {
                slots.add(new int[]{i, current.getAmount()});
            }
        }

        // Sort them so we get the one with the lowest quantity first
        slots.sort(Comparator.comparingInt(slot -> slot[1]));
        return slots;
    }

    private int getMultiplierAndRemoveResources(ItemStack[] inventoryContents, List<int[]> slotsWithAmount, int maxMultiplier) {
        if (slotsWithAmount.isEmpty()) {
            return -1;
        }

        // Calculate all slots we need to remove and check how many times this purchase will be done
        int resourcesFound = 0;
        int multiplier = 0;

        int lastSlot = -1;
        ItemStack itemInLastSlot = null;

        for (int[] slotAndAmount : slotsWithAmount) {
            int slot = slotAndAmount[0];
            resourcesFound += slotAndAmount[1];

            lastSlot = slot;
            itemInLastSlot = inventoryContents[slot];
            inventoryContents[slot] = null;

            while (resourcesFound >= this.price.getAmount()) {
                resourcesFound -= this.price.getAmount();
                multiplier++;

                if (multiplier >= maxMultiplier) break;
            }

            if (multiplier >= maxMultiplier) break;
        }

        if (multiplier == 0) {
            return -1;
        }

        // Clear the slots and set the last one to the rest of it
        if (resourcesFound > 0) {
            itemInLastSlot = itemInLastSlot.clone();
            itemInLastSlot.setAmount(resourcesFound);
            inventoryContents[lastSlot] = itemInLastSlot;
        }

        return multiplier;
    }

}
