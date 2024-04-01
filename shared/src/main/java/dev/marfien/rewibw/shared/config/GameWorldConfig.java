package dev.marfien.rewibw.shared.config;

import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Data
@ConfigSerializable
public class GameWorldConfig {

    private WorldConfig world = new WorldConfig();

}
