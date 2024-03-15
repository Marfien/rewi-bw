package dev.marfien.rewibw.shop;

import dev.marfien.rewibw.util.IntPair;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.LinkedList;
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

        List<IntPair> slots = new LinkedList<>();

        // Collect all the slots with matching resource type
        for (int i = 0; i < inventoryContents.length; i++) {
            ItemStack current = inventoryContents[i];
            if (current != null && price.getType().getMaterial() == current.getType()) {
                slots.add(new IntPair(i, current.getAmount()));
            }
        }

        // Sort them so we get the one with the lowest quantity first
        slots.sort(Comparator.comparingInt(IntPair::getSecond));

        // Calculate all slots we need to remove and check how many times this purchase will be done
        int resourcesFound = 0;
        int multiplier = 0;
        LinkedList<IntPair> usedSlots = new LinkedList<>();
        for (IntPair slot : slots) {
            // first: slot index
            // second: amount
            resourcesFound += slot.getSecond();
            usedSlots.add(slot);

            while (resourcesFound >= price.getAmount()) {
                resourcesFound -= price.getAmount();
                multiplier++;

                if (multiplier >= maxMultiplier) break;
            }

            if (multiplier >= maxMultiplier) break;
        }

        if (multiplier == 0) {
            clicker.sendMessage("§b[Shop] §cDu hast nicht genug " + price.getType().getTranslation() + ".");
            clicker.playSound(clicker.getLocation(), Sound.NOTE_BASS_GUITAR, 1, 1);
            return;
        }

        IntPair last = usedSlots.getLast();

        // Clear the slots and set the last one to the rest of it
        int rest = resourcesFound;
        usedSlots.forEach(slot -> {
            int inventorySlot = slot.getFirst();
            if (rest > 0 && slot == last) {
                inventoryContents[inventorySlot].setAmount(rest);
            } else {
                inventoryContents[inventorySlot] = null;
            }
        });

        playerInventory.setContents(inventoryContents);

        clicker.playSound(clicker.getLocation(), Sound.ITEM_PICKUP, 1, 1);
        this.giveToPlayer(clicker, multiplier, event.getHotbarButton());
    }

}
