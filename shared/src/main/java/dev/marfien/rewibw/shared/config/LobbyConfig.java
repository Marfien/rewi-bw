package dev.marfien.rewibw.shared.config;

import dev.marfien.rewibw.shared.Position;
import dev.marfien.rewibw.shared.TeamColor;
import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.Map;
import java.util.Optional;

@Data
@ConfigSerializable
public class LobbyConfig extends GameWorldConfig {

    @Required
    private Position spawn;
    private Position cpsTester;
    @Required
    private Map<TeamColor, LobbyTeamConfig> teams;

    private JumpAndRunConfig jumpAndRun;

    public Optional<Position> getCpsTester() {
        return Optional.ofNullable(this.cpsTester);
    }

    @Data
    @ConfigSerializable
    public static class LobbyTeamConfig {

        private Position joiner;
        private Position[] displays;

    }

    @Data
    @ConfigSerializable
    public static class JumpAndRunConfig {

        private Position start;
        private Position finish;

    }

}
