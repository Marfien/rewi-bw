package dev.marfien.rewibw;

import dev.marfien.rewibw.shared.TeamColor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@ConfigSerializable
public class RewiBWConfig {

    private TeamConfig teams = new TeamConfig();
    private VoteConfig voting = new VoteConfig();

    private Path lobbyMap = Paths.get("/", "data", "lobby");

    private Path mapPool = Paths.get("/", "data", "maps");

    @Getter
    @ConfigSerializable
    public static class TeamConfig {

        private int playersPerTeam = 2;
        private TeamColor[] variants = {TeamColor.RED, TeamColor.GREEN, TeamColor.PURPLE, TeamColor.YELLOW};

        public int getMaxPlayers() {
            return this.playersPerTeam * this.variants.length;
        }

        public int getMinPlayers() {
            return this.getMaxPlayers() / 2;
        }

    }

    @Getter
    @ConfigSerializable
    public static class VoteConfig {

        private int[] inventorySlots = {1, 4, 7};

    }
}
