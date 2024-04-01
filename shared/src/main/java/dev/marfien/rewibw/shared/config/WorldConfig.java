package dev.marfien.rewibw.shared.config;

import dev.marfien.rewibw.shared.world.GameWorldGenerator;
import lombok.Data;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Data
@ConfigSerializable
public class WorldConfig {

    private GameWorldGenerator generator = GameWorldGenerator.EMPTY;
    private World.Environment environment = World.Environment.NORMAL;
    private Long initTime = null;
    private Difficulty difficulty = Difficulty.NORMAL;

}
