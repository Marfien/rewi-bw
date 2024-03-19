package dev.marfien.rewibw.shared.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface GuiItem {

    ItemStack getDisplayItemFor(Player player);

    void onClick(GuiInventory inventory, InventoryClickEvent click);

}
