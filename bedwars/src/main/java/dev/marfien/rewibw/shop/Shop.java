package dev.marfien.rewibw.shop;

import dev.marfien.rewibw.ResourceType;
import dev.marfien.rewibw.shared.gui.GuiInventory;
import dev.marfien.rewibw.team.TeamManager;
import dev.marfien.rewibw.shared.InventoryUtil;
import dev.marfien.rewibw.shared.ItemBuilder;
import dev.marfien.rewibw.util.Items;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Shop {

    private static final ShopCategory[] CATEGORIES = new ShopCategory[]{
            new ShopCategory("Blöcke", Material.RED_SANDSTONE,
                    new ShopItem(ResourceType.BRONZE.withAmount(1), Items.RED_SANDSTONE, 32),
                    new ShopItem(ResourceType.BRONZE.withAmount(7), Items.ENDSTONE, 64),
                    new ShopItem(ResourceType.SILVER.withAmount(3), Items.IRON_BLOCK, 64),
                    new ShopItem(ResourceType.BRONZE.withAmount(4),
                            player -> ItemBuilder.of(Material.STAINED_GLASS, TeamManager.getTeam(player).getColor().getDyeColor().getData(), 1)
                                    .setDisplayName(ChatColor.GRAY + "Glas")
                                    .asItemStack(),
                            64),
                    new ShopItem(ResourceType.BRONZE.withAmount(16), ItemBuilder.of(Material.GLOWSTONE, 4).setDisplayName(ChatColor.GRAY + "Glowstone").asItemStack(), 16)
            ),
            new ShopCategory("Rüstung", Material.CHAINMAIL_CHESTPLATE,
                    new ShopItem(ResourceType.BRONZE.withAmount(1),
                            player -> ItemBuilder.of(Material.LEATHER_HELMET)
                                    .setDisplayName(ChatColor.BLUE + "Lederhelm")
                                    .setLeatherArmorColor(TeamManager.getTeam(player).getColor().getDyeColor().getColor())
                                    .asItemStack(), 1),
                    new ShopItem(ResourceType.BRONZE.withAmount(1),
                            player -> ItemBuilder.of(Material.LEATHER_LEGGINGS)
                                    .setDisplayName(ChatColor.BLUE + "Lederhose")
                                    .setLeatherArmorColor(TeamManager.getTeam(player).getColor().getDyeColor().getColor())
                                    .asItemStack(), 1),
                    new ShopItem(ResourceType.BRONZE.withAmount(1),
                            player -> ItemBuilder.of(Material.LEATHER_BOOTS)
                                    .setDisplayName(ChatColor.BLUE + "Lederschuhe")
                                    .setLeatherArmorColor(TeamManager.getTeam(player).getColor().getDyeColor().getColor())
                                    .asItemStack(), 1),
                    new ShopItem(ResourceType.SILVER.withAmount(1), ItemBuilder.of(Material.CHAINMAIL_CHESTPLATE).setDisplayName(ChatColor.BLUE + "Brustplatte I").asItemStack(),
                            1),
                    new ShopItem(ResourceType.SILVER.withAmount(3),
                            ItemBuilder.of(Material.CHAINMAIL_CHESTPLATE)
                                    .setDisplayName(ChatColor.BLUE + "Brustplatte II")
                                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                                    .asItemStack(),
                            1),
                    new ShopItem(ResourceType.SILVER.withAmount(7),
                            ItemBuilder.of(Material.CHAINMAIL_CHESTPLATE)
                                    .setDisplayName(ChatColor.BLUE + "Brustplatte III")
                                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                                    .asItemStack(),
                            1),
                    new ShopItem(ResourceType.SILVER.withAmount(11),
                            ItemBuilder.of(Material.CHAINMAIL_CHESTPLATE)
                                    .setDisplayName(ChatColor.BLUE + "Brustplatte IV")
                                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                                    .addEnchantment(Enchantment.THORNS, 1)
                                    .asItemStack(),
                            1)
            ),
            new ShopCategory("Spitzhacken", Material.IRON_PICKAXE,
                    new ShopItem(ResourceType.BRONZE.withAmount(7), ItemBuilder.of(Material.WOOD_PICKAXE).setDisplayName(ChatColor.YELLOW + "Spitzhacke I").asItemStack(), 1),
                    new ShopItem(ResourceType.SILVER.withAmount(2), ItemBuilder.of(Material.STONE_PICKAXE).setDisplayName(ChatColor.YELLOW + "Spitzhacke II").asItemStack(), 1),
                    new ShopItem(ResourceType.GOLD.withAmount(1), ItemBuilder.of(Material.IRON_PICKAXE).setDisplayName(ChatColor.YELLOW + "Spitzhacke III").asItemStack(), 1)
            ),
            new ShopCategory("Schwerter", Material.WOOD_SWORD,
                    new ShopItem(ResourceType.BRONZE.withAmount(10),
                            ItemBuilder.of(Material.STICK)
                                    .setDisplayName(ChatColor.RED + "Knüppel")
                                    .addEnchantment(Enchantment.KNOCKBACK, 1)
                                    .asItemStack(),
                            1),
                    new ShopItem(ResourceType.SILVER.withAmount(1), ItemBuilder.of(Material.WOOD_SWORD).setDisplayName(ChatColor.RED + "Schwert I").asItemStack(), 1),
                    new ShopItem(ResourceType.SILVER.withAmount(3),
                            ItemBuilder.of(Material.WOOD_SWORD)
                                    .setDisplayName(ChatColor.RED + "Schwert II")
                                    .addEnchantment(Enchantment.DAMAGE_ALL, 1)
                                    .asItemStack(),
                            1),
                    new ShopItem(ResourceType.SILVER.withAmount(5),
                            ItemBuilder.of(Material.WOOD_SWORD)
                                    .setDisplayName(ChatColor.RED + "Schwert III")
                                    .addEnchantment(Enchantment.DAMAGE_ALL, 2)
                                    .asItemStack(), 1),
                    new ShopItem(ResourceType.GOLD.withAmount(5),
                            ItemBuilder.of(Material.IRON_SWORD)
                                    .setDisplayName(ChatColor.RED + "Schwert IV")
                                    .addEnchantment(Enchantment.DAMAGE_ALL, 1)
                                    .addEnchantment(Enchantment.KNOCKBACK, 1)
                                    .asItemStack(), 1)

            ),
            new ShopCategory("Bögen", Material.BOW,
                    new ShopItem(ResourceType.GOLD.withAmount(3),
                            ItemBuilder.of(Material.BOW)
                                    .setDisplayName(ChatColor.DARK_PURPLE + "Bogen I")
                                    .setDurability((short) 120)
                                    .addEnchantment(Enchantment.ARROW_INFINITE, 1)
                                    .asItemStack(), 1),
                    new ShopItem(ResourceType.GOLD.withAmount(7),
                            ItemBuilder.of(Material.BOW)
                                    .setDisplayName(ChatColor.DARK_PURPLE + "Bogen II")
                                    .setDurability((short) 120)
                                    .addEnchantment(Enchantment.ARROW_INFINITE, 1)
                                    .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
                                    .asItemStack(), 1),
                    new ShopItem(ResourceType.GOLD.withAmount(11),
                            ItemBuilder.of(Material.BOW)
                                    .setDisplayName(ChatColor.DARK_PURPLE + "Bogen III")
                                    .setDurability((short) 120)
                                    .addEnchantment(Enchantment.ARROW_INFINITE, 1)
                                    .addEnchantment(Enchantment.ARROW_DAMAGE, 2)
                                    .addEnchantment(Enchantment.ARROW_KNOCKBACK, 1)
                                    .asItemStack(), 1),
                    Shoppable.SPACER,
                    new ShopItem(ResourceType.GOLD.withAmount(1), ItemBuilder.of(Material.ARROW).setDisplayName(ChatColor.DARK_PURPLE + "Pfeil").asItemStack(), 1)
            ),
            new ShopCategory("Essen", Material.CAKE,
                    new ShopItem(ResourceType.BRONZE.withAmount(2), ItemBuilder.of(Material.APPLE).setDisplayName(ChatColor.DARK_GREEN + "Apfel").asItemStack(), 64),
                    new ShopItem(ResourceType.BRONZE.withAmount(4), ItemBuilder.of(Material.COOKED_BEEF).setDisplayName(ChatColor.DARK_GREEN + "Streak").asItemStack(), 64),
                    new ShopItem(ResourceType.SILVER.withAmount(1), ItemBuilder.of(Material.CAKE).setDisplayName(ChatColor.DARK_GREEN + "Kuchen").asItemStack(), 64),
                    Shoppable.SPACER,
                    new ShopItem(ResourceType.GOLD.withAmount(2), ItemBuilder.of(Material.GOLDEN_APPLE).setDisplayName(ChatColor.DARK_GREEN + "Goldapfel").asItemStack(), 64)
            ),
            new ShopCategory("Kisten", Material.ENDER_CHEST,
                    new ShopItem(ResourceType.SILVER.withAmount(3), Items.CHEST, 64),
                    new ShopItem(ResourceType.GOLD.withAmount(1), Items.TEAM_CHEST, 64)
            ),
            new ShopCategory("Tränke", Material.POTION, item -> item.setDurability((short) 8230),
                    new ShopItem(ResourceType.SILVER.withAmount(5),
                            ItemBuilder.of(Material.POTION, (short) 8261)
                                    .setDisplayName(ChatColor.LIGHT_PURPLE + "Heilung I")
                                    .addCustomPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 0))
                                    .asItemStack(),
                            1),
                    new ShopItem(ResourceType.SILVER.withAmount(7),
                            ItemBuilder.of(Material.POTION, (short) 8229)
                                    .setDisplayName(ChatColor.LIGHT_PURPLE + "Heilung II")
                                    .addCustomPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 1))
                                    .asItemStack(),
                            1),
                    new ShopItem(ResourceType.SILVER.withAmount(10),
                            ItemBuilder.of(Material.POTION, (short) 8203)
                                    .setDisplayName(ChatColor.LIGHT_PURPLE + "Jumpboost")
                                    .addCustomPotionEffect(new PotionEffect(PotionEffectType.JUMP, 3600, 0))
                                    .asItemStack(),
                            1),
                    new ShopItem(ResourceType.GOLD.withAmount(5),
                            ItemBuilder.of(Material.POTION, (short) 8201)
                                    .setDisplayName(ChatColor.LIGHT_PURPLE + "Stärke")
                                    .addCustomPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 3600, 0))
                                    .asItemStack(),
                            1)
            ),
            new ShopCategory("Extras", Material.EMERALD,
                    new ShopItem(ResourceType.BRONZE.withAmount(4), Items.LADDER, 64),
                    new ShopItem(ResourceType.SILVER.withAmount(10), Items.TELEPORTER, 64),
                    new ShopItem(ResourceType.SILVER.withAmount(12), Items.MOBILE_SHOP, 64),
                    new ShopItem(ResourceType.GOLD.withAmount(5), Items.TNT, 64),
                    new ShopItem(ResourceType.GOLD.withAmount(2), Items.PARACHUTE, 64),
                    new ShopItem(ResourceType.GOLD.withAmount(3), Items.RESCUE_PLATFORM, 64),
                    new ShopItem(ResourceType.GOLD.withAmount(14), ItemBuilder.of(Material.ENDER_PEARL).setDisplayName(ChatColor.GOLD + "Enderperle").asItemStack(), 64),
                    new ShopItem(ResourceType.BRONZE.withAmount(20), ItemBuilder.of(Material.WEB).setDisplayName(ChatColor.GOLD + "Spinnennetz").asItemStack(), 64)
            )
    };

    private static final GuiInventory NO_CATEGORY;

    static {
        int[] categoryIndexes = InventoryUtil.calcCenterIndexes(CATEGORIES.length);
        int categoryRows = InventoryUtil.rowsNeeded(CATEGORIES.length);
        ShopCategory[] indexedShopCategories = new ShopCategory[categoryRows * 9];

        for (int i = 0; i < CATEGORIES.length; i++) {
            indexedShopCategories[categoryIndexes[i]] = CATEGORIES[i];
        }

        for (ShopCategory category : CATEGORIES) {
            category.init(indexedShopCategories);
        }

        NO_CATEGORY = new GuiInventory(categoryRows + 1, ChatColor.DARK_AQUA + "BedWars Shop");
        NO_CATEGORY.setContents(indexedShopCategories);
    }

    public static void open(Player player) {
        NO_CATEGORY.openTo(player);
    }
}
