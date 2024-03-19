package dev.marfien.rewibw.setuptool;

import dev.marfien.rewibw.setuptool.item.TeamSpawnAdder;
import dev.marfien.rewibw.shared.ItemBuilder;
import dev.marfien.rewibw.shared.TeamColor;
import dev.marfien.rewibw.shared.gui.GuiInventory;
import dev.marfien.rewibw.shared.gui.GuiItem;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class TeamSelectorGuiItem implements GuiItem {

    private final TeamColor color;

    @Override
    public ItemStack getDisplayItemFor(Player player) {
        return ItemBuilder.of(Material.BANNER)
                .setDurability(color.getDyeColor().getDyeData())
                .setDisplayName(color.getDisplayName()).asItemStack();
    }

    @Override
    public void onClick(GuiInventory inventory, InventoryClickEvent click) {
        Player clicker = (Player) click.getWhoClicked();
        SetupSession session = SetupToolPlugin.getSession(clicker);
        if (session == null) return;

        inventory.closeSafe(clicker);
        session.getTeams().put(color, new SetupSession.TeamInfo());
        clicker.sendMessage("§aTeam " + color.getDisplayName() + " hinzugefügt");
        clicker.getInventory().setItem(7, TeamSpawnAdder.getItemFor(color));
    }
}
