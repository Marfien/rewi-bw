package dev.marfien.rewibw.shared.config;

import dev.marfien.rewibw.shared.Position;
import dev.marfien.rewibw.shared.TeamColor;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.util.Map;

@Data
@ConfigSerializable
public class MapConfig extends GameWorldConfig {

    public static ConfigurationLoader<?> loader(Path folder) {
        return YamlConfigurationLoader.builder()
                .path(folder.resolve("config.yaml"))
                .build();
    }

    @Required
    private MapInfo map;

    @Required
    private Position spectatorSpawn;

    private Position[] shops = new Position[0];

    private SpawnerConfig spawner = new SpawnerConfig();

    @Required
    private Map<TeamColor, MapTeamConfig> teams;

    @Data
    @ConfigSerializable
    public static class MapInfo {

        private String icon;
        private String displayName;
        private String builder;

        private BorderConfig border;

        public ItemStack getIcon() {
            String[] iconString = this.icon.split(":");
            Material icon = Material.matchMaterial(iconString[0]);
            short iconData = iconString.length > 1 ? Short.parseShort(iconString[1]) : 0;

            return new ItemStack(icon, 1, iconData);
        }

    }

    @Data
    @ConfigSerializable
    public static class BorderConfig {

        private int x1;
        private int x2;
        private int z1;
        private int z2;

        public BorderSnapshot getSnapshot() {
            return new BorderSnapshot(
                    Math.min(this.x1, this.x2),
                    Math.max(this.x1, this.x2),
                    Math.min(this.z1, this.z2),
                    Math.max(this.z1, this.z2)
            );
        }

    }

    @Data
    @ConfigSerializable
    public static class SpawnerConfig {

        private Position[] bronze = new Position[0];
        private Position[] silver = new Position[0];
        private Position[] gold = new Position[0];

    }

    @Data
    public static class BorderSnapshot {

        private final int lowerX;
        private final int upperX;

        private final int lowerZ;
        private final int upperZ;

    }

    @Data
    @ConfigSerializable
    public static class MapTeamConfig {

        private Position spawn;
        private TeamBedConfig bed;

    }

    @Data
    @ConfigSerializable
    public static class TeamBedConfig {

        private Position bed;
        private BlockFace direction;

    }

}
