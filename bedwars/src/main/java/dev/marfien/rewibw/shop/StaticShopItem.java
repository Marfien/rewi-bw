package dev.marfien.rewibw.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StaticShopItem extends ShopItem {

    private final ItemStack item;
    private final ItemStack displayItem;

    public StaticShopItem(ItemStack item, ShopPrice price, int shiftClickMultiplier) {
        super(price, shiftClickMultiplier);
        this.item = item;
        this.displayItem = item.clone();
        super.addPriceToLore(this.displayItem);
    }

    @Override
    public ItemStack createItem(Player player) {
        return this.item.clone();
    }

    @Override
    public ItemStack getDisplayItemFor(Player player) {
        return this.displayItem;
    }
}
