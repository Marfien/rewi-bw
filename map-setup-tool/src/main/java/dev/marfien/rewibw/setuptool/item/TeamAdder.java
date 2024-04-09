package dev.marfien.rewibw.setuptool.item;

import dev.marfien.rewibw.setuptool.SetupSessionManager;
import dev.marfien.rewibw.setuptool.TeamSelectorGuiItem;
import dev.marfien.rewibw.shared.ItemBuilder;
import dev.marfien.rewibw.shared.TeamColor;
import dev.marfien.rewibw.shared.gui.GuiInventory;
import dev.marfien.rewibw.shared.gui.GuiItem;
import dev.marfien.rewibw.shared.usable.ConsumeType;
import dev.marfien.rewibw.shared.usable.UsableItemInfo;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TeamAdder extends UsableItemInfo {

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

    public TeamAdder() {
        super(ConsumeType.NONE);
    }

    @Override
    protected boolean onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        SetupSessionManager.getSession(player.getUniqueId()).ifPresent(session -> {
            if (!player.getWorld().equals(session.getWorld())) return;

            GUI.openTo(player);
        });

        return false;
    }

}
