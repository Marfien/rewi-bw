package dev.marfien.rewibw.world;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.nio.file.Path;

@Getter
@RequiredArgsConstructor
public class GameMapInfo {

    private final Path path;
    private final String name;
    private final String displayName;
    private final String builder;

    private final Material icon;
    private final short iconData;

    public ItemStack getIcon() {
        return new ItemStack(this.icon, 1, this.iconData);
    }


    static GameMapInfo fromConfig(Path path) {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(path.resolve("config.yaml").toFile());
        String name = path.getFileName().toString();
        String displayName = configuration.getString("map.displayName");
        String builder = configuration.getString("map.builder");
        String[] iconString = configuration.getString("map.icon").split(":");
        Material icon = Material.getMaterial(iconString[0]);
        short iconData = iconString.length > 1 ? Short.parseShort(iconString[1]) : 0;

        return new GameMapInfo(path, name, displayName, builder, icon, iconData);
    }

}
