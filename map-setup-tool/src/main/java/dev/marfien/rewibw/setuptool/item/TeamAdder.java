package dev.marfien.rewibw.setuptool.item;

import dev.marfien.rewibw.setuptool.SetupSession;
import dev.marfien.rewibw.setuptool.SetupToolPlugin;
import dev.marfien.rewibw.shared.ItemBuilder;
import dev.marfien.rewibw.shared.TeamColor;
import dev.marfien.rewibw.shared.gui.GuiInventory;
import dev.marfien.rewibw.shared.gui.GuiItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TeamAdder extends SessionItem {

    private static final GuiInventory GUI;

    static {
        TeamColor[] values = TeamColor.values();
        GuiItem[] items = new GuiItem[values.length];
        for (int i = 0; i < values.length; i++) {
            TeamColor color = values[i];
            items[i] = new GuiItem() {
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
            };
        }

        GUI = new GuiInventory(items, "§aTeam wählen");
    }

    public static final ItemStack ITEM = ItemBuilder.of(Material.BANNER).setDisplayName("§aTeam hinzufügen").asItemStack();

    @Override
    protected void onClick(PlayerInteractEvent event, Player player, SetupSession session, Location location) {
        GUI.openTo(player);
    }

}
