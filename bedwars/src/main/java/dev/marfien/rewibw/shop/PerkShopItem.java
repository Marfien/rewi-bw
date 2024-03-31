package dev.marfien.rewibw.shop;

import dev.marfien.rewibw.perk.ItemPerkGroup;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PerkShopItem extends ShopItem {

    private final ItemPerkGroup perkGroup;
    private final ItemStack base;

    public PerkShopItem(ItemPerkGroup perkGroup, ItemStack base, ShopPrice price, int shiftClickMultiplier) {
        super(price, shiftClickMultiplier);
        this.perkGroup = perkGroup;
        this.base = base;
        perkGroup.getDefaultPerk().transformItem(base);
        super.addPriceToLore(base);
    }

    @Override
    public ItemStack createItem(Player player) {
        ItemStack item = this.base.clone();
        this.perkGroup.getOrDefault(player).transformItem(item);
        return item;
    }

    @Override
    public ItemStack getDisplayItemFor(Player player) {
        return this.base;
    }
}
