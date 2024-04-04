package dev.marfien.rewibw.voting;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.shared.ItemBuilder;
import dev.marfien.rewibw.shared.config.MapConfig;
import dev.marfien.rewibw.shared.gui.GuiInventory;
import dev.marfien.rewibw.shared.gui.GuiItem;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;


@RequiredArgsConstructor
public class VotingGuiItem implements GuiItem {

    private final int slot;
    private final ItemStack displayItem;
    private final String displayName;
    private final String mapName;

    public VotingGuiItem(int slot, String name, MapConfig.MapInfoConfig mapInfo) {
        this.slot = slot;
        this.mapName = name;
        this.displayName = mapInfo.getDisplayName();
        ItemBuilder displayIconBuilder = ItemBuilder.of(mapInfo.getIcon())
                .setDisplayName(ChatColor.GRAY + this.displayName);

        if (mapInfo.getBuilder() != null) {
                displayIconBuilder.setLore("§8von " + mapInfo.getBuilder());
        }

        this.displayItem = displayIconBuilder.asItemStack();
    }

    @Override
    public ItemStack getDisplayItemFor(Player player) {
        return ItemBuilder.of(this.displayItem.clone())
                .addLoreLines("", "§f" + MapVoting.getVotes(this.mapName) + " Votes")
                .asItemStack();
    }

    @Override
    public void onClick(GuiInventory inventory, InventoryClickEvent click) {
        Player player = (Player) click.getWhoClicked();
        MapVoting.vote(player, this.mapName);
        inventory.update(slot);

        player.playSound(player.getLocation(), Sound.ENDERDRAGON_HIT, 1.0F, 1.0F);
        if (MapVoting.hasVoted(player)) {
            player.sendMessage(RewiBWPlugin.PREFIX + Message.VOTE_CHANGED.format(this.displayName));
        } else {
            player.sendMessage(RewiBWPlugin.PREFIX + Message.VOTE_CAST.format(this.displayName));
        }
        player.closeInventory();
    }
}
