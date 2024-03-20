package dev.marfien.rewibw.perk;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class MaterialPerk extends AbstractPerk<Material> {

    private final Listener listener = new MaterialPerkListener();

    public MaterialPerk(Material defaultPerk) {
        super(defaultPerk, MaterialPerkListener.class);
    }

    @Override
    public Listener getListener() {
        return this.listener;
    }

    public class MaterialPerkListener implements Listener {

        @EventHandler
        private void onDrop(PlayerDropItemEvent event) {
            Item dropped = event.getItemDrop();
            ItemStack droppedItemStack = dropped.getItemStack();
            Player player = event.getPlayer();

            if (droppedItemStack.getType() != getPerk(player)) return;

            this.resetItem(droppedItemStack);
            dropped.setItemStack(droppedItemStack);
        }

        @EventHandler
        private void onPickup(PlayerPickupItemEvent event) {
            Item item = event.getItem();
            ItemStack itemStack = item.getItemStack();
            Player player = event.getPlayer();

            if (itemStack.getType() != getDefault()) return;

            this.setItem(player, itemStack);
            item.setItemStack(itemStack);
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
        private void onMoveOutOfInventory(InventoryMoveItemEvent event) {
            Inventory source = event.getSource();
            if (!(source instanceof PlayerInventory)) return;

            HumanEntity owner = ((PlayerInventory) source).getHolder();
            if (!(owner instanceof Player)) return;
            Player player = (Player) owner;

            ItemStack itemStack = event.getItem();
            if (itemStack.getType() != getPerk(player)) return;
            resetItem(itemStack);
            event.setItem(itemStack);
        }

        @EventHandler
        private void onMoveIntoInventory(InventoryClickEvent event) {
            // TODO: check if this is the correct way to check for inventory move
            switch (event.getAction()) {
                // item clicked is set into inventory
                case PLACE_ALL:
                case PLACE_ONE:
                case PLACE_SOME:
                // item in cursor is set to inventory
                case SWAP_WITH_CURSOR:
                case MOVE_TO_OTHER_INVENTORY:
                case HOTBAR_MOVE_AND_READD:
                case HOTBAR_SWAP:
                    break;
                default: return;
            }

            ItemStack itemStack = event.getCurrentItem();
        }

        private void resetItem(ItemStack itemStack) {
            itemStack.setType(getDefault());
        }

        private void setItem(Player player, ItemStack itemStack) {
            itemStack.setType(getOrDefault(player));
        }

    }

}
