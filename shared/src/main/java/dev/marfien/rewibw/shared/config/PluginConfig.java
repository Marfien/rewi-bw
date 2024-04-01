package dev.marfien.rewibw.shared.config;

import dev.marfien.rewibw.shared.TeamColor;
import lombok.Data;
import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

@Data
@ConfigSerializable
public class PluginConfig {

    public static ConfigurationLoader<?> loader(Path folder) {
        return YamlConfigurationLoader.builder()
                .path(folder.resolve("config.yaml"))
                .build();
    }

    public static ConfigurationLoader<?> loader(Plugin plugin) {
        return loader(plugin.getDataFolder().toPath());
    }

    @Required
    private TeamConfig teams;
    @Required
    private VoteConfig voting;

    @Required
    private Path lobbyMap;

    @Required
    private Path mapPool;

    @Data
    @ConfigSerializable
    public static class TeamConfig {

        @Required
        private int playersPerTeam;
        @Required
        private TeamColor[] variants;

        public int getMaxPlayers() {
            return this.playersPerTeam * this.variants.length;
        }

        public int getMinPlayers() {
            return this.getMaxPlayers() / 2;
        }

    }

    @Data
    @ConfigSerializable
    public static class VoteConfig {

        @Required
        private int[] inventorySlots;

    }
}
