package dev.marfien.rewibw.shop;

import dev.marfien.rewibw.Message;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public interface Shoppable extends ShopButton {

    Shoppable SPACER = new Shoppable() {
        @Override
        public ShopPrice getPrice() {
            return null;
        }

        @Override
        public void giveToPlayer(Player player, int multiplier, int slot) {

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
        }
    };

    ShopPrice getPrice();

    void giveToPlayer(Player player, int multiplier, int slot);

    int getShiftClickMultiplier();

    @Override
    default void onClick(InventoryClickEvent event) {
        Player clicker = (Player) event.getWhoClicked();
        Inventory playerInventory = clicker.getInventory();
        ShopPrice price = this.getPrice();

        int maxMultiplier = event.isShiftClick()
                ? this.getShiftClickMultiplier()
                : 1;

        ItemStack[] inventoryContents = playerInventory.getContents();

        List<int[]> slots = new ArrayList<>(inventoryContents.length / 2);

        // Collect all the slots with matching resource type
        for (int i = 0; i < inventoryContents.length; i++) {
            ItemStack current = inventoryContents[i];
            if (current != null && price.getType().getMaterial() == current.getType()) {
                slots.add(new int[]{i, current.getAmount()});
            }
        }

        if (slots.isEmpty()) {
            clicker.sendMessage("§b[Shop] " + Message.SHOP_NOT_ENOUGH_RESOURCES.format(price.getType().getTranslation()));
            clicker.playSound(clicker.getLocation(), Sound.NOTE_BASS_GUITAR, 1, 1);
            return;
        }

        // Sort them so we get the one with the lowest quantity first
        slots.sort(Comparator.comparingInt(slot -> slot[1]));

        // Calculate all slots we need to remove and check how many times this purchase will be done
        int resourcesFound = 0;
        int multiplier = 0;

        int index = 0;
        int[] usedSlots = new int[slots.size()];
        int lastSlot = -1;
        for (int[] slotAndAmount : slots) {
            resourcesFound += slotAndAmount[1];
            usedSlots[index] = slotAndAmount[0];
            lastSlot = index++;

            while (resourcesFound >= price.getAmount()) {
                resourcesFound -= price.getAmount();
                multiplier++;

                if (multiplier >= maxMultiplier) break;
            }

            if (multiplier >= maxMultiplier) break;
        }

        if (multiplier == 0) {
            clicker.sendMessage("§b[Shop] " + Message.SHOP_NOT_ENOUGH_RESOURCES.format(price.getType().getTranslation()));
            clicker.playSound(clicker.getLocation(), Sound.NOTE_BASS_GUITAR, 1, 1);
            return;
        }

        System.out.println("last slot: " + lastSlot);
        System.out.println("Array: " + Arrays.toString(usedSlots));

        // Clear the slots and set the last one to the rest of it
        int rest = resourcesFound;
        for (int slot : usedSlots) {
            if (rest > 0 && slot == lastSlot) {
                inventoryContents[slot].setAmount(rest);
            } else {
                inventoryContents[slot] = null;
            }
        }

        playerInventory.setContents(inventoryContents);

        clicker.playSound(clicker.getLocation(), Sound.ITEM_PICKUP, 1, 1);
        this.giveToPlayer(clicker, multiplier, event.getHotbarButton());
    }

}
