package dev.marfien.rewibw.shared;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ItemBuilder {

    private final ItemStack itemStack;

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(new ItemStack(material));
    }

    public static ItemBuilder of(ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }

    public static ItemBuilder of(Material material, short damage) {
        return new ItemBuilder(new ItemStack(material, 1, damage));
    }

    public static ItemBuilder of(Material material, int amount) {
        return new ItemBuilder(new ItemStack(material, amount));
    }

    public static ItemBuilder of(Material material, short damage, int amount) {
        return new ItemBuilder(new ItemStack(material, amount, damage));
    }

    private ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemBuilder setMaterial(Material material) {
        return this.useItemStack(itemStack -> itemStack.setType(material));
    }

    public ItemBuilder setAmount(int amount) {
        return this.useItemStack(itemStack -> itemStack.setAmount(amount));
    }

    public ItemBuilder setDamage(short damage) {
        return this.useItemStack(itemStack -> itemStack.setDurability(damage));
    }

    public ItemBuilder setDurability(short damage) {
        return this.useItemStack(itemStack -> itemStack.setDurability((short) (itemStack.getType().getMaxDurability() - damage)));
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        return this.useItemStack(itemStack -> itemStack.addUnsafeEnchantment(enchantment, level));
    }

    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        return this.useItemStack(itemStack -> itemStack.removeEnchantment(enchantment));
    }

    public ItemBuilder setMaterialData(MaterialData data) {
        return this.useItemStack(itemStack -> itemStack.setData(data));
    }

    public ItemBuilder setDisplayName(String name) {
        return this.useItemMeta(meta -> meta.setDisplayName(name));
    }

    public ItemBuilder setLore(List<String> lore) {
        return this.useItemMeta(meta -> meta.setLore(lore));
    }

    public ItemBuilder setLore(String... lore) {
        return this.setLore(Arrays.asList(lore));
    }

    public ItemBuilder setLore(String lore) {
        return this.setLore(lore.split("\n"));
    }

    public ItemBuilder addLoreLines(String... lines) {
        return this.useItemMeta(meta -> {
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.addAll(Arrays.asList(lines));
            meta.setLore(lore);
        });
    }

    public ItemBuilder removeLoreLine(int index) {
        return this.useItemMeta(meta -> {
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.remove(index);
            meta.setLore(lore);
        });
    }

    public ItemBuilder addItemFlags(ItemFlag... flags) {
        return this.useItemMeta(meta -> meta.addItemFlags(flags));
    }

    public ItemBuilder removeItemFlags(ItemFlag... flags) {
        return this.useItemMeta(meta -> meta.removeItemFlags(flags));
    }

    public ItemBuilder setUnbreakable() {
        return this.useItemMeta(meta -> meta.spigot().setUnbreakable(true));
    }

    public ItemBuilder setSkullOwner(String owner) {
        return this.useSkullMetaSafe(meta -> meta.setOwner(owner));
    }

    public ItemBuilder setLeatherArmorColor(Color color) {
        return this.useLeatherArmorMetaSafe(meta -> meta.setColor(color));
    }

    public ItemBuilder setMainPotionEffect(PotionEffectType effectType) {
        return this.usePotionMetaSafe(meta -> meta.setMainEffect(effectType));
    }

    public ItemBuilder addCustomPotionEffect(PotionEffect effect) {
        return this.usePotionMetaSafe(meta -> meta.addCustomEffect(effect, true));
    }

    public ItemBuilder removeCustomPotionEffect(PotionEffectType effectType) {
        return this.usePotionMetaSafe(meta -> meta.removeCustomEffect(effectType));
    }

    public ItemBuilder setBannerBaseColor(DyeColor color) {
        return useBannerMetaSafe(bannerMeta -> bannerMeta.setBaseColor(color));
    }

    public ItemStack asItemStack() {
        return this.itemStack;
    }

    private ItemBuilder useItemStack(Consumer<ItemStack> consumer) {
        consumer.accept(this.itemStack);
        return this;
    }

    private ItemBuilder useItemMeta(Consumer<ItemMeta> consumer) {
        ItemMeta meta = this.itemStack.getItemMeta();
        consumer.accept(meta);
        this.itemStack.setItemMeta(meta);
        return this;
    }

    private ItemBuilder useSkullMetaSafe(Consumer<SkullMeta> consumer) {
        ItemMeta meta = this.itemStack.getItemMeta();

        if (!(meta instanceof SkullMeta)) return this;

        consumer.accept((SkullMeta) meta);
        this.itemStack.setItemMeta(meta);
        return this;
    }

    private ItemBuilder useLeatherArmorMetaSafe(Consumer<LeatherArmorMeta> consumer) {
        ItemMeta meta = this.itemStack.getItemMeta();

        if (!(meta instanceof LeatherArmorMeta)) return this;

        consumer.accept((LeatherArmorMeta) meta);
        this.itemStack.setItemMeta(meta);
        return this;
    }

    private ItemBuilder usePotionMetaSafe(Consumer<PotionMeta> consumer) {
        ItemMeta meta = this.itemStack.getItemMeta();

        if (!(meta instanceof PotionMeta)) return this;

        consumer.accept((PotionMeta) meta);
        this.itemStack.setItemMeta(meta);
        return this;
    }

    private ItemBuilder useBannerMetaSafe(Consumer<BannerMeta> consumer) {
        ItemMeta meta = this.itemStack.getItemMeta();

        if (!(meta instanceof BannerMeta)) return this;

        consumer.accept((BannerMeta) meta);
        this.itemStack.setItemMeta(meta);
        return this;
    }
}
