package dev.marfien.rewibw.shared.gui;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class StaticNoOpGuiItem implements NoOpGuiItem {

    private final ItemStack displayItem;

    @Override
    public ItemStack getDisplayItemFor(Player player) {
        return this.displayItem;
    }
}
