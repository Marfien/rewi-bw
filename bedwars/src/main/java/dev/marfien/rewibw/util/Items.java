package dev.marfien.rewibw.util;

import dev.marfien.rewibw.shared.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Items {

    // Lobby Items
    public static final ItemStack QUIT_ITEM = ItemBuilder.of(Material.SLIME_BALL).setDisplayName("§eVerlassen").asItemStack();
    public static final ItemStack VOTE_ITEM = ItemBuilder.of(Material.PAPER).setDisplayName("§eMap-Voting").asItemStack();
    public static final ItemStack PERKS_ITEM = ItemBuilder.of(Material.DRAGON_EGG).setDisplayName("§ePerks").asItemStack();

    public static final ItemStack SPECTATOR_COMPASS = ItemBuilder.of(Material.COMPASS).setDisplayName("§eSpielerübersicht").asItemStack();

    // Shop Items
    public static final ItemStack RED_SANDSTONE = ItemBuilder.of(Material.RED_SANDSTONE, 2).setDisplayName(ChatColor.GRAY + "Sandstein").asItemStack();
    public static final ItemStack ENDSTONE = ItemBuilder.of(Material.ENDER_STONE, 1).setDisplayName(ChatColor.GRAY + "Endstein").asItemStack();
    public static final ItemStack IRON_BLOCK = ItemBuilder.of(Material.IRON_BLOCK, 1).setDisplayName(ChatColor.GRAY + "Eisenblock").asItemStack();
    public static final ItemStack CHEST = ItemBuilder.of(Material.CHEST).setDisplayName(ChatColor.GREEN + "Kiste").asItemStack();
    public static final ItemStack TEAM_CHEST = ItemBuilder.of(Material.ENDER_CHEST).setDisplayName(ChatColor.GREEN + "Teamkiste").asItemStack();
    public static final ItemStack LADDER = ItemBuilder.of(Material.LADDER).setDisplayName(ChatColor.GOLD + "Leiter").asItemStack();
    public static final ItemStack TELEPORTER = ItemBuilder.of(Material.FIREWORK).setDisplayName(ChatColor.GOLD + "Teleporter").asItemStack();
    public static final ItemStack MOBILE_SHOP = ItemBuilder.of(Material.ARMOR_STAND).setDisplayName(ChatColor.GOLD + "Mobiler Shop").asItemStack();
    public static final ItemStack TNT = ItemBuilder.of(Material.TNT).setDisplayName(ChatColor.GOLD + "TNT").asItemStack();
    public static final ItemStack PARACHUTE = ItemBuilder.of(Material.MONSTER_EGG).setDisplayName(ChatColor.GOLD + "Fallschirm").asItemStack();
    public static final ItemStack RESCUE_PLATFORM = ItemBuilder.of(Material.NETHER_STAR).setDisplayName(ChatColor.GOLD + "Rettungsplattform").asItemStack();
}
