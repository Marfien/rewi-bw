package dev.marfien.rewibw.shared.config;

import dev.marfien.rewibw.shared.Position;
import dev.marfien.rewibw.shared.TeamColor;
import lombok.Data;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.util.Map;

@Data
@ConfigSerializable
public class LobbyConfig extends GameWorldConfig {

    @Required
    private Position spawn;
    private Position cpsTester;
    @Required
    private Map<TeamColor, LobbyTeamConfig> teams;

    @Data
    @ConfigSerializable
    public static class LobbyTeamConfig {

        private Position joiner;
        private Position[] displays;

    }
}
