package dev.marfien.rewibw.world;

import dev.marfien.rewibw.shared.Position;
import dev.marfien.rewibw.shared.config.GameWorldConfig;
import dev.marfien.rewibw.shared.config.WorldConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

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

    public World load() {
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

        return this.world;
    }

    public void unload() {
        if (this.world == null) return;
        Bukkit.unloadWorld(this.world, false);
    }

    public Location asLocation(Function<T, Position> positionFunction) {
        return positionFunction.apply(this.config).toLocation(this.world);
    }

}
