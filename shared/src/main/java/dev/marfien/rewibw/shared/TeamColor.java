package dev.marfien.rewibw.shared;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

@Getter
@RequiredArgsConstructor
public enum TeamColor {

    RED(ChatColor.RED, DyeColor.RED, "Rot"),
    YELLOW(ChatColor.YELLOW, DyeColor.YELLOW, "Gelb"),
    PURPLE(ChatColor.DARK_PURPLE, DyeColor.PURPLE, "Lila"),
    GREEN(ChatColor.GREEN, DyeColor.LIME, "Grün"),

    WHITE(ChatColor.WHITE, DyeColor.WHITE, "Weiß"),
    LIGHT_BLUE(ChatColor.BLUE, DyeColor.LIGHT_BLUE, "Blau"),
    ORANGE(ChatColor.GOLD, DyeColor.ORANGE, "Orange"),
    BLACK(ChatColor.BLACK, DyeColor.BLACK, "Schwarz"),

    MAGENTA(ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA, "Magenta"),
    PINK(ChatColor.LIGHT_PURPLE, DyeColor.PINK, "Pink"),
    GRAY(ChatColor.DARK_GRAY, DyeColor.GRAY, "Dunkelgrau"),
    SILVER(ChatColor.GRAY, DyeColor.SILVER, "Hellgrau"),
    CYAN(ChatColor.AQUA, DyeColor.CYAN, "Hellblau"),
    BLUE(ChatColor.DARK_BLUE, DyeColor.BLUE, "Dunkelblau"),
    BROWN(ChatColor.DARK_RED, DyeColor.BROWN, "Braun"),
    LIME(ChatColor.DARK_GREEN, DyeColor.GREEN, "Dunkelgrün");


    private final ChatColor chatColor;
    private final DyeColor dyeColor;

    private final String name;

    public String getDisplayName() {
        return this.chatColor + this.name;
    }

}
