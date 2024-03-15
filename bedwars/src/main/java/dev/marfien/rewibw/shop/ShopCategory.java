package dev.marfien.rewibw.shop;

import dev.marfien.rewibw.gui.GuiInventory;
import dev.marfien.rewibw.gui.GuiItem;
import dev.marfien.rewibw.gui.NoOpGuiItem;
import dev.marfien.rewibw.util.InventoryUtil;
import dev.marfien.rewibw.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Consumer;

@Getter
public class ShopCategory implements ShopButton, Listener {

    private final Shoppable[] shoppables;
    private final ItemStack displayItem;
    private final Consumer<ItemStack> transformOnActive;

    private GuiInventory inventory;

    public ShopCategory(String title, Material material, Consumer<ItemStack> transformOnActive, Shoppable... shoppables) {
        this.displayItem = ItemBuilder.of(material).setDisplayName(ChatColor.AQUA + title).asItemStack();
        this.transformOnActive = transformOnActive;
        this.shoppables = shoppables;
    }

    public ShopCategory(String title, Material material, Shoppable... shoppables) {
        this(title, material, item -> {
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
            item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        }, shoppables);
    }

    void init(ShopCategory[] indexedShopCategory) {
        if (this.inventory != null)
            throw new IllegalStateException("ShopCategory already initialized");

        int[] indexes = InventoryUtil.calcCenterIndexes(this.shoppables.length);
        GuiItem[] contents = new GuiItem[InventoryUtil.rowsNeeded(indexes.length) * 9 + indexedShopCategory.length];

        for (int i = 0; i < indexedShopCategory.length; i++) {
            ShopCategory category = indexedShopCategory[i];

            if (category != this) {
                contents[i] = category;
                continue;
            }

            contents[i] = (NoOpGuiItem) player -> {
                ItemStack displayItem = category.getDisplayItemFor(player);
                this.transformOnActive.accept(displayItem);
                return displayItem;
            };
        }

        int offset = indexedShopCategory.length;
        for (int i = 0; i < indexes.length; i++) {
            contents[indexes[i] + offset] = this.shoppables[i];
        }

        this.inventory = new GuiInventory(contents, ChatColor.DARK_AQUA + "BedWars Shop");
    }

    @Override
    public ItemStack getDisplayItemFor(Player player) {
        return this.displayItem.clone();
    }

    public Shoppable[] getShoppables() {
        return this.shoppables.clone();
    }

    public int getShoppablesSize() {
        return this.shoppables.length;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (this.inventory == null) throw new IllegalStateException("ShopCategory not initialized yet.");

        Player player = (Player) event.getWhoClicked();
        player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
        this.inventory.openTo((Player) event.getWhoClicked());
    }
}
