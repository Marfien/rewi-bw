package dev.marfien.rewibw.game.playing.item;

import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.GameStateManager;
import dev.marfien.rewibw.game.playing.PlayingGameState;
import dev.marfien.rewibw.team.GameTeam;
import dev.marfien.rewibw.team.TeamManager;
import dev.marfien.rewibw.usable.ConsumeType;
import dev.marfien.rewibw.usable.UsableItemInfo;
import dev.marfien.rewibw.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;

public class SpectatorCompass extends UsableItemInfo {

    private static final Inventory SPECTATOR_INVENTORY = Bukkit.createInventory(null, NumberConversions.ceil(RewiBWPlugin.getMaxPlayers() / 9D) * 9, "§8Spielerliste");

    public SpectatorCompass() {
        super(ConsumeType.NONE);
    }

    @Override
    protected boolean onClick(PlayerInteractEvent event) {
        return super.onClick(event);
    }

    public static void refreshInventory() {
        ItemStack[] contents = new ItemStack[SPECTATOR_INVENTORY.getSize()];
        int slot = 0;
        for (GameTeam team : TeamManager.getTeams()) {
            for (Player player : team.getMembers()) {
                contents[slot++] = ItemBuilder.of(Material.SKULL_ITEM)
                        .setSkullOwner(player.getName())
                        .setDisplayName(player.getDisplayName())
                        .setLore(" ", " §fClicke zum teleportieren")
                        .asItemStack();
            }
        }
        SPECTATOR_INVENTORY.setContents(contents);
    }

}
