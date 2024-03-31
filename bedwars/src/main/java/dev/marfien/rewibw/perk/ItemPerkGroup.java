package dev.marfien.rewibw.perk;

import dev.marfien.rewibw.RewiBWPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

public class ItemPerkGroup extends PerkGroup<ItemStackTransformPerk> {

    public ItemPerkGroup(ItemStack displayItem, ItemStackTransformPerk defaultPerk, ItemStackTransformPerk... perks) {
        super(displayItem, defaultPerk, perks);
    }

    @Override
    public void init(Plugin plugin, PerkGroup<?>[] perkGroups) {
        super.init(plugin, perkGroups);
        Bukkit.getPluginManager().registerEvents(new MaterialPerkListener(), plugin);
    }

    private class MaterialPerkListener implements Listener {

        private void resetItem(Player player, ItemStack item) {
            getPerk(player).ifPresent(perk -> {
                if (perk.isSimilar(item)) {
                    getDefaultPerk().transformItem(item);
                }
            });
        }

        private void applyPerk(Player player, ItemStack item) {
            if (!getDefaultPerk().isSimilar(item)) return;

            getPerk(player).ifPresent(perk -> perk.transformItem(item));
        }

        @EventHandler
        private void onDrop(PlayerDropItemEvent event) {
            Item dropped = event.getItemDrop();
            ItemStack droppedItemStack = dropped.getItemStack();
            Player player = event.getPlayer();

            resetItem(player, droppedItemStack);
            dropped.setItemStack(droppedItemStack);
        }

        @EventHandler
        private void onPickup(PlayerPickupItemEvent event) {
            Item item = event.getItem();
            ItemStack pickedUpItemStack = item.getItemStack();
            Player player = event.getPlayer();

            applyPerk(player, pickedUpItemStack);
            item.setItemStack(pickedUpItemStack);
        }

        @EventHandler(ignoreCancelled = true)
        private void onMoveIntoInventory(InventoryClickEvent event) {
            HumanEntity human = event.getWhoClicked();
            if (!(human instanceof Player)) return;
            Player player = (Player) human;
            Inventory inventory = event.getClickedInventory();
            int slot = event.getSlot();

            boolean isPlayerInventory = inventory instanceof PlayerInventory;
            switch (event.getAction()) {
                case PLACE_ALL:
                case PLACE_ONE:
                case PLACE_SOME:
                case SWAP_WITH_CURSOR:
                    Bukkit.getScheduler().runTaskLater(RewiBWPlugin.getInstance(), () -> {
                        if (isPlayerInventory) {
                            applyPerk(player, inventory.getItem(slot));
                        } else {
                            resetItem(player, inventory.getItem(slot));
                        }
                    }, 1);
                    break;
                case MOVE_TO_OTHER_INVENTORY:
                    if (isPlayerInventory) {
                        resetItem(player, event.getCurrentItem());
                    } else {
                        applyPerk(player, event.getCurrentItem());
                    }
                    break;
                case HOTBAR_SWAP:
                    if (isPlayerInventory) return;
                    int hotbarSlot = event.getHotbarButton();
                    ItemStack inOtherInventory = inventory.getItem(slot);
                    ItemStack inPlayerInventory = player.getInventory().getItem(hotbarSlot);

                    resetItem(player, inPlayerInventory);
                    applyPerk(player, inOtherInventory);
                    Bukkit.getScheduler().runTaskLater(RewiBWPlugin.getInstance(), player::updateInventory, 1);
                    break;
            }
        }

        @EventHandler
        private void onInteract(PlayerInteractEvent event) {
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

            if (getOrDefault(event.getPlayer()).isSimilar(event.getItem())) {
                event.setUseItemInHand(Event.Result.DENY);
            }
        }

    }


}
