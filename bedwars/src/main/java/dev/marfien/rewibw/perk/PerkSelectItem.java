package dev.marfien.rewibw.perk;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.shared.ItemBuilder;
import dev.marfien.rewibw.shared.gui.GuiInventory;
import dev.marfien.rewibw.shared.gui.GuiItem;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class PerkSelectItem implements GuiItem {

    @Override
    public ItemStack getDisplayItemFor(Player player) {
        ItemStack displayItem = perk.getDisplayItem().clone();

        getPerk(player).ifPresent(selectedPerk -> {
            if (selectedPerk == perk) {
                ItemBuilder.of(displayItem)
                        .setLore(" ", "§a§lAusgewählt")
                        .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                        .addEnchantment(Enchantment.DURABILITY, 1);
            }
        });
        return displayItem;
    }

    @Override
    public void onClick(GuiInventory inventory, InventoryClickEvent click) {
        Player clicker = (Player) click.getWhoClicked();
        PerkGroup.this.setPerk(clicker, (P) perk);
        clicker.closeInventory();
        clicker.sendMessage(RewiBWPlugin.PREFIX + Message.SELECT_PERK.format(ChatColor.stripColor(perk.getName())));
    }
}
