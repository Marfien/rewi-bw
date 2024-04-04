package dev.marfien.rewibw.perk;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.shared.ItemBuilder;
import dev.marfien.rewibw.shared.gui.GuiInventory;
import dev.marfien.rewibw.shared.gui.GuiItem;
import dev.marfien.rewibw.shared.gui.NoOpGuiItem;
import dev.marfien.rewibw.shared.gui.StaticNoOpGuiItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public abstract class PerkGroup<P extends Perk> implements GuiItem {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final GuiItem PANE = new StaticNoOpGuiItem(ItemBuilder.of(Material.STAINED_GLASS_PANE).setDisplayName("§7").setDamage((short) 7).asItemStack());
    private static final GuiItem PANE_ACTIVE = new StaticNoOpGuiItem(ItemBuilder.of(Material.STAINED_GLASS_PANE).setDisplayName("§7").setDamage((short) 13).asItemStack());
    private static final GuiItem PANE_INACTIVE = new StaticNoOpGuiItem(ItemBuilder.of(Material.STAINED_GLASS_PANE).setDisplayName("§7").setDamage((short) 14).asItemStack());

    private final String key;
    private final ItemStack displayItem;
    private final P defaultPerk;

    private final P[] perks;
    @Getter(AccessLevel.NONE)
    private final Map<Player, P> selectedPerks = new HashMap<>();

    private GuiInventory inventory;

    @SafeVarargs
    protected PerkGroup(String key, ItemStack displayItem, P defaultPerk, P... perks) {
        this.key = key;
        this.displayItem = displayItem;
        this.defaultPerk = defaultPerk;
        this.perks = perks;
    }

    public void setPerk(Player player, P perk) {
        LOGGER.debug("Perk set to %s for %s in %s", perk.getName(), player.getName(), this.key);
        this.selectedPerks.put(player, perk);
    }

    public void unsetPerk(Player player) {
        LOGGER.trace("Unsetting perk for player %s", player.getName());
        this.selectedPerks.remove(player);
    }

    public Optional<P> getPerk(Player player) {
        return Optional.ofNullable(this.selectedPerks.get(player));
    }

    public P getOrDefault(Player player) {
        return getPerk(player).orElse(this.defaultPerk);
    }

    @Override
    public ItemStack getDisplayItemFor(Player player) {
        return this.displayItem;
    }

    @Override
    public void onClick(GuiInventory inventory, InventoryClickEvent click) {
        this.openGui((Player) click.getWhoClicked());
    }

    public void init(Plugin plugin, PerkGroup<?>[] perkGroups) {
        if (this.inventory != null)
            throw new IllegalStateException("ShopCategory already initialized");

        GuiItem[] contents = new GuiItem[6 * 9];

        for (int i = 0; i < 9; i++) {
            contents[i] = PANE;
            contents[contents.length - 9 + i] = PANE;
        }

        // perk group selection
        for (int i = 0; i < perkGroups.length; i++) {
            PerkGroup<?> perkGroup = perkGroups[i];
            int row = i + 1;

            if (perkGroup != this) {
                contents[row * 9 + 1] = PANE_INACTIVE;
                contents[row * 9] = perkGroup;
                continue;
            }

            contents[row * 9 + 1] = PANE_ACTIVE;
            contents[row * 9] = (NoOpGuiItem) player -> {
                ItemStack displayItem = perkGroup.getDisplayItemFor(player).clone();
                ItemMeta meta = displayItem.getItemMeta();
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                displayItem.setItemMeta(meta);
                displayItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                return displayItem;
            };
        }

        Perk[][] perkRows = new Perk[NumberConversions.ceil(this.perks.length / 4D)][4];
        for (int i = 0; i < this.perks.length; i++) {
            perkRows[i / 4][i % 4] = this.perks[i];
        }

        int startRow = NumberConversions.floor(6 / 2D - perkRows.length / 2D);
        for (int rwo = 0; rwo < perkRows.length; rwo++) {
            Perk[] perkRow = perkRows[rwo];
            int row = startRow + rwo;

            for (int collumn = 0; collumn < perkRow.length; collumn++) {
                Perk perk = perkRow[collumn];
                if (perk == null) continue;

                int col = 3 + collumn;
                int slot = row * 9 + col;
                contents[slot] = new PerkSelectGuiItem((P) perk);
            }
        }

        // reset button
        contents[4*9 + 8] = new ResetPerkGuiItem();

        this.inventory = new GuiInventory(contents, ChatColor.GOLD + "Perks");
    }

    public void openGui(Player player) {
        this.inventory.openTo(player);
    }

    public void destroyGui() {
        this.inventory.destory();
        this.inventory = null;
    }

    @RequiredArgsConstructor
    public class PerkSelectGuiItem implements GuiItem {

        private final P perk;

        @Override
        public ItemStack getDisplayItemFor(Player player) {
            ItemStack displayItem = this.perk.getDisplayItem().clone();

            getPerk(player).ifPresent(selectedPerk -> {
                if (selectedPerk == this.perk) {
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
            PerkGroup.this.setPerk(clicker, this.perk);
            clicker.closeInventory();
            clicker.sendMessage(RewiBWPlugin.PREFIX + Message.SELECT_PERK.format(ChatColor.stripColor(this.perk.getName())));
        }
    }

    private class ResetPerkGuiItem implements GuiItem {

        private final ItemStack displayItem = ItemBuilder.of(Material.BARRIER).setDisplayName("§cZurücksetzen").asItemStack();

        @Override
        public ItemStack getDisplayItemFor(Player player) {
            return this.displayItem;
        }

        @Override
        public void onClick(GuiInventory inventory, InventoryClickEvent click) {
            Player player = (Player) click.getWhoClicked();
            PerkGroup.this.unsetPerk(player);
            player.sendMessage(RewiBWPlugin.PREFIX + Message.RESET_PERK);
            player.closeInventory();
        }
    }

}
