package dev.marfien.rewibw.shop;

import dev.marfien.rewibw.gui.GuiInventory;
import dev.marfien.rewibw.gui.GuiItem;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface ShopButton extends GuiItem {

    @Override
    default void onClick(GuiInventory inventory, InventoryClickEvent event) {
        this.onClick(event);
    }

    void onClick(InventoryClickEvent event);
}
