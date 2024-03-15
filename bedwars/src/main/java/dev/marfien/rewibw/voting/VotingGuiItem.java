package dev.marfien.rewibw.voting;

import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.gui.GuiInventory;
import dev.marfien.rewibw.gui.GuiItem;
import dev.marfien.rewibw.util.ItemBuilder;
import dev.marfien.rewibw.world.GameMapInfo;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;


@RequiredArgsConstructor
public class VotingGuiItem implements GuiItem {

    private final int slot;
    private final GameMapInfo map;

    @Override
    public ItemStack getDisplayItemFor(Player player) {
        ItemBuilder builder = ItemBuilder.of(this.map.getIcon())
                .setDisplayName(ChatColor.GRAY + this.map.getDisplayName())
                .setLore("§f" + MapVoting.getVotes(this.map) + " Votes");

        if (map.getBuilder() != null) {
            builder.addLoreLine("§8von " + map.getBuilder());
        }

        return builder.asItemStack();
    }

    @Override
    public void onClick(GuiInventory inventory, InventoryClickEvent click) {
        Player player = (Player) click.getWhoClicked();
        MapVoting.vote(player, this.map);
        inventory.update(slot);

        player.playSound(player.getLocation(), Sound.ENDERDRAGON_HIT, 1.0F, 1.0F);
        if (MapVoting.hasVoted(player)) {
            player.sendMessage(RewiBWPlugin.PREFIX + "Du hast Deine Stimme auf die Map §a" + ChatColor.stripColor(this.map.getDisplayName()) + " §7geändert.");
        } else {
            player.sendMessage(RewiBWPlugin.PREFIX + "Du hast für die Map §a" + ChatColor.stripColor(map.getDisplayName()) + " §7gestimmt.");
        }
        player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 1.0F);
        player.closeInventory();
    }
}