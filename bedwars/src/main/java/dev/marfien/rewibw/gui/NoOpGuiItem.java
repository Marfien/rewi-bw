package dev.marfien.rewibw.gui;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface NoOpGuiItem extends GuiItem {

    @Override
    default void onClick(GuiInventory inventory, InventoryClickEvent click) {
        // no op
    }
}
