package dev.marfien.rewibw.shop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class DynamicShopItem extends ShopItem {

    private final Function<Player, ItemStack> factory;

    public DynamicShopItem(Function<Player, ItemStack> factory, ShopPrice price, int shiftClickMultiplier) {
        super(price, shiftClickMultiplier);
        this.factory = factory;
    }

    @Override
    public ItemStack createItem(Player player) {
        return this.factory.apply(player);
    }

    @Override
    public ItemStack getDisplayItemFor(Player player) {
        ItemStack item = this.createItem(player);
        super.addPriceToLore(item);
        return item;
    }
}
