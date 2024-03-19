package dev.marfien.rewibw.setuptool.item;

import dev.marfien.rewibw.setuptool.SetupSession;
import dev.marfien.rewibw.setuptool.TeamSelectorGuiItem;
import dev.marfien.rewibw.shared.ItemBuilder;
import dev.marfien.rewibw.shared.TeamColor;
import dev.marfien.rewibw.shared.gui.GuiInventory;
import dev.marfien.rewibw.shared.gui.GuiItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TeamAdder extends SessionItem {

    private static final GuiInventory GUI;

    static {
        TeamColor[] values = TeamColor.values();
        GuiItem[] items = new GuiItem[values.length];
        for (int i = 0; i < values.length; i++) {
            TeamColor color = values[i];
            items[i] = new TeamSelectorGuiItem(color);
        }

        GUI = new GuiInventory(items, "§aTeam wählen");
    }

    public static final ItemStack ITEM = ItemBuilder.of(Material.BANNER).setDisplayName("§aTeam hinzufügen").asItemStack();

    @Override
    protected void onClick(PlayerInteractEvent event, Player player, SetupSession session, Location location) {
        GUI.openTo(player);
    }

}
