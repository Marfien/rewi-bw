package dev.marfien.rewibw.shared.usable;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class UsableItemManager {

    private final HashMap<ItemStackHashWrapper, UsableItemInfo> handlers = new HashMap<>();
    private final EventListener listener;

    public UsableItemManager() {
        this.listener = this.new EventListener();
    }

    public void register(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this.listener, plugin);
    }

    public void shutdown() {
        HandlerList.unregisterAll(this.listener);
        this.handlers.clear();
    }

    public void putHandler(ItemStack item, UsableItemInfo handler) {
        this.handlers.put(new ItemStackHashWrapper(item), handler);
    }

    public boolean addHandler(ItemStack item, UsableItemInfo handler, boolean force) {
        ItemStackHashWrapper wrapper = new ItemStackHashWrapper(item);

        if (!force && this.handlers.containsKey(wrapper)) return false;

        this.handlers.put(wrapper, handler);
        return true;
    }

    public UsableItemInfo getHandler(ItemStack item) {
        return this.handlers.get(new ItemStackHashWrapper(item));
    }

    public boolean hasHandler(ItemStack itemStack) {
        return this.handlers.containsKey(new ItemStackHashWrapper(itemStack));
    }

    private class EventListener implements Listener {

        @EventHandler
        private void onInteract(PlayerInteractEvent event) {
            if (!this.isUsingItem(event)) return;

            ItemStack item = event.getItem();
            UsableItemInfo info = getHandler(item);
            if (info == null) return;
            event.setCancelled(true);

            if (info.onClick(event)) {
                event.getPlayer().setItemInHand(info.getConsumeType().consumeItem(item));
            }
        }

        private boolean isUsingItem(PlayerInteractEvent event) {
            Action action = event.getAction();
            return (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && event.hasItem();
        }

    }

}
