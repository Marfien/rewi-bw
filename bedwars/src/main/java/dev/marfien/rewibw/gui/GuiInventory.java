package dev.marfien.rewibw.gui;

import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.util.InventoryUtil;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class GuiInventory {

    private final GuiItem[] items;
    private final Function<Player, String> titleFactory;

    private final Map<Player, Inventory> cache = new ConcurrentHashMap<>();
    private final boolean useCache;

    public GuiInventory(int rows, Function<Player, String> titleFactory, boolean useCache) {
        this.items = new GuiItem[rows * 9];
        this.titleFactory = titleFactory;
        this.useCache = useCache;

        Bukkit.getPluginManager().registerEvents(this.new EventListener(), RewiBWPlugin.getInstance());
    }

    public GuiInventory(int rows, Function<Player, String> titleFactory) {
        this(rows, titleFactory, true);
    }

    public GuiInventory(int rows, String title) {
        this(rows, ignored -> title);
    }

    public GuiInventory(GuiItem[] contents, Function<Player, String> titleFactory, boolean useCache) {
        this(InventoryUtil.rowsNeeded(contents.length), titleFactory, useCache);

        System.arraycopy(contents, 0, this.items, 0, contents.length);
    }

    public GuiInventory(GuiItem[] contents, Function<Player, String> titleFactory) {
        this(contents, titleFactory, true);
    }

    public GuiInventory(GuiItem[] contents, String title) {
        this(contents, ignored -> title);
    }

    public void setContents(GuiItem[] contents) {
        if (contents.length > this.items.length) throw new IndexOutOfBoundsException("Gui has a size of " + this.items.length + " and contents has a size of " + contents.length);

        synchronized (this) {
            for (int i = 0; i < this.items.length; i++) {
                this.items[i] = i < contents.length ? contents[i] : null;
            }

            this.cache.forEach((player, inventory) -> {
                ItemStack[] newContents = Arrays.stream(contents)
                        .map(guiItem -> guiItem.getDisplayItemFor(player))
                        .toArray(ItemStack[]::new);

                inventory.setContents(newContents);
            });
        }
    }

    public void update(int slot) {
        if (slot < 0 || slot >= this.items.length) throw new IndexOutOfBoundsException("Index: " + slot + ", array size: " + this.items.length);

        for (Map.Entry<Player, Inventory> entry : this.cache.entrySet()) {
            Player player = entry.getKey();
            Inventory value = entry.getValue();
            value.setItem(slot, this.items[slot].getDisplayItemFor(player));
        }
    }

    public void setItem(int index, GuiItem item) {
        if (index < 0 || index >= this.items.length)
            throw new IndexOutOfBoundsException("Index: " + index + ", array size: " + this.items.length);

        synchronized (this) {
            this.items[index] = item;
            for (Map.Entry<Player, Inventory> entry : this.cache.entrySet()) {
                Player player = entry.getKey();
                Inventory value = entry.getValue();
                value.setItem(index, item.getDisplayItemFor(player));
            }
        }
    }

    public GuiItem getItem(int index) {
        if (index < 0 || index >= this.items.length)
            throw new IndexOutOfBoundsException("Index: " + index + ", array size: " + this.items.length);
        return this.items[index];
    }

    public int size() {
        return this.items.length;
    }

    public Collection<Inventory> getOpenInventories() {
        return this.cache.values();
    }

    public void closeAll() {
        this.cache.keySet().forEach(Player::closeInventory);
    }

    public void closeSafe(Player player) {
        if (this.cache.containsKey(player)) {
            player.closeInventory();
        }
    }

    public void openTo(Player player) {
        if (this.useCache && this.cache.containsKey(player)) {
            player.openInventory(this.cache.get(player));
            return;
        }

        ItemStack[] contents = new ItemStack[this.items.length];
        for (int i = 0; i < this.items.length; i++) {
            GuiItem item = this.items[i];

            if (item == null) continue;

            contents[i] = item.getDisplayItemFor(player);
        }

        Inventory inventory = Bukkit.createInventory(new GuiInventoryHolder(player, this), this.size(), titleFactory.apply(player));
        inventory.setContents(contents);

        player.openInventory(inventory);
    }

    public boolean controlsInventory(Inventory inventory) {
        return this.cache.containsValue(inventory);
    }

    private class EventListener implements Listener {

        @EventHandler
        private void onInventoryInteract(InventoryClickEvent event) {
            Inventory inventory = event.getClickedInventory();
            if (inventory == null) return;
            InventoryHolder holder = inventory.getHolder();

            if (!(holder instanceof GuiInventoryHolder)) return;
            GuiInventoryHolder guiHolder = (GuiInventoryHolder) holder;

            if (guiHolder.guiInventory != GuiInventory.this) return;

            event.setCancelled(true);

            synchronized (GuiInventory.this) {
                GuiItem clickedItem = items[event.getSlot()];
                if (clickedItem == null) return;
                clickedItem.onClick(GuiInventory.this, event);
            }
        }

        @EventHandler
        private void onClose(InventoryCloseEvent event) {
            // Every inventory is created per player
            if (!useCache)
                cache.remove((Player) event.getPlayer());
        }

        @EventHandler
        private void onDisconnect(PlayerQuitEvent event) {
            cache.remove(event.getPlayer());
        }
    }

    @AllArgsConstructor
    private static class GuiInventoryHolder implements InventoryHolder {

        private final Player player;
        private final GuiInventory guiInventory;

        @Override
        public Inventory getInventory() {
            return this.player.getInventory();
        }
    }

}