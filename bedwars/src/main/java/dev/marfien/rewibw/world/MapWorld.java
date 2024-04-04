package dev.marfien.rewibw.world;

import dev.marfien.rewibw.ResourceType;
import dev.marfien.rewibw.fakeentities.FakeEntityManager;
import dev.marfien.rewibw.shared.Position;
import dev.marfien.rewibw.shared.config.MapConfig;
import dev.marfien.rewibw.shop.FakeDealer;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;

public class MapWorld extends GameWorld<MapConfig> {

    private Collection<BukkitTask> spawnerTasks = new ArrayList<>(3);

    public MapWorld(String name, MapConfig config) {
        super(name, config);
    }

    @Override
    public World load() {
        World world = super.load();

        MapConfig.SpawnerConfig spawnerConfig = this.getConfig().getSpawner();
        this.spawnerTasks.add(ResourceType.BRONZE.startSpawning(world, spawnerConfig.getBronze()));
        this.spawnerTasks.add(ResourceType.SILVER.startSpawning(world, spawnerConfig.getSilver()));
        this.spawnerTasks.add(ResourceType.GOLD.startSpawning(world, spawnerConfig.getGold()));

        for (Position shop : this.getConfig().getShops()) {
            FakeEntityManager.spawn(new FakeDealer(shop.toLocation(world)));
        }

        return world;
    }

    @Override
    public void unload() {
        this.spawnerTasks.forEach(BukkitTask::cancel);
        super.unload();
    }
}
