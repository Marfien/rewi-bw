package dev.marfien.rewibw.perk;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

public class ItemPerkGroup extends PerkGroup<DataPerk<ItemStack>> {

    @SafeVarargs
    public ItemPerkGroup(ItemStack displayItem, DataPerk<ItemStack> defaultPerk, DataPerk<ItemStack>... perks) {
        super(displayItem, defaultPerk, perks);
    }

    @Override
    public void init(Plugin plugin, PerkGroup<?>[] perkGroups) {
        super.init(plugin, perkGroups);
        Bukkit.getPluginManager().registerEvents(new MaterialPerkListener(), plugin);
    }

    public class MaterialPerkListener implements Listener {

        @EventHandler
        private void onDrop(PlayerDropItemEvent event) {
            Item dropped = event.getItemDrop();
            ItemStack droppedItemStack = dropped.getItemStack();
            Player player = event.getPlayer();

            getPerk(player).ifPresent(perk -> {
                ItemStack perkItemStack = perk.getData();
                if (!droppedItemStack.isSimilar(perkItemStack)) return;

                ItemStack newItemStack = getDefaultPerk().getData().clone();
                newItemStack.setAmount(droppedItemStack.getAmount());
                dropped.setItemStack(newItemStack);
            });
        }

        @EventHandler
        private void onPickup(PlayerPickupItemEvent event) {
            Item item = event.getItem();
            ItemStack pickedUpItemStack = item.getItemStack();
            Player player = event.getPlayer();

            if (!pickedUpItemStack.isSimilar(getDefaultPerk().getData())) return;

            getPerk(player).ifPresent(perk -> {
                ItemStack perkItemStack = perk.getData();

                ItemStack newItemStack = perkItemStack.clone();
                newItemStack.setAmount(pickedUpItemStack.getAmount());
                item.setItemStack(newItemStack);
            });
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
                default:
                    return;
            }

            ItemStack itemStack = event.getCurrentItem();
            HumanEntity human = event.getWhoClicked();

            if (!(human instanceof Player)) return;
            Player player = (Player) human;

            if (event.getClickedInventory() instanceof PlayerInventory) {
                getPerk(player).ifPresent(perk -> {
                    ItemStack perkItemStack = perk.getData();
                    if (!itemStack.isSimilar(perkItemStack)) return;

                    ItemStack newItemStack = perkItemStack.clone();
                    newItemStack.setAmount(itemStack.getAmount());
                    event.setCurrentItem(newItemStack);
                });
            } else {
                ItemStack defaultItemStack = getDefaultPerk().getData();
                if (!itemStack.isSimilar(defaultItemStack)) return;

                ItemStack newItemStack = defaultItemStack.clone();
                newItemStack.setAmount(itemStack.getAmount());
                event.setCurrentItem(newItemStack);
            }
        }

    }


}
