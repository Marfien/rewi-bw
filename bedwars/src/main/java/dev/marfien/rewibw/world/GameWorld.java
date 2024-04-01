package dev.marfien.rewibw.world;

import dev.marfien.rewibw.shared.config.GameWorldConfig;
import dev.marfien.rewibw.shared.config.WorldConfig;
import dev.marfien.rewibw.shared.world.GameWorldGenerator;
import dev.marfien.rewibw.shared.Position;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Function;

@Getter
public class GameWorld<T extends GameWorldConfig> {

    private final String name;
    private final T config;

    private World world;


    public GameWorld(String name, T config) {
        this.name = name;
        this.config = config;
    }

    public void load() {
        WorldConfig worldConfig = this.config.getWorld();

        this.world = WorldCreator.name(this.name)
                .generator(worldConfig.getGenerator().getGenerator())
                .environment(worldConfig.getEnvironment())
                .createWorld();

        this.world.setKeepSpawnInMemory(false);
        this.world.setSpawnFlags(false, false);
        this.world.setDifficulty(worldConfig.getDifficulty());

        if (worldConfig.getInitTime() != null) {
            this.world.setTime(worldConfig.getInitTime());
        }
    }

    public Location asLocation(Function<T, Position> positionFunction) {
        return positionFunction.apply(this.config).toLocation(this.world);
    }

}
