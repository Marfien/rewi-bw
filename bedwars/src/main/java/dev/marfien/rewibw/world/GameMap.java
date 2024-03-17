package dev.marfien.rewibw.world;

import dev.marfien.rewibw.ResourceType;
import dev.marfien.rewibw.fakemobs.FakeEntityManager;
import dev.marfien.rewibw.shop.FakeDealer;
import dev.marfien.rewibw.team.GameTeam;
import dev.marfien.rewibw.team.TeamBed;
import dev.marfien.rewibw.team.TeamManager;
import lombok.Getter;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Getter
public class GameMap extends GameWorld {

    private final GameMapInfo mapInfo;

    private final Map<ResourceType, Collection<Location>> spawnerLocations = new EnumMap<>(ResourceType.class);

    private final int borderUpperX;
    private final int borderLowerX;
    private final int borderUpperZ;
    private final int borderLowerZ;

    public GameMap(String name, GameMapInfo info) {
        super(name);
        this.mapInfo = info;
        int x1 = super.getInt("map.border.x1");
        int x2 = super.getInt("map.border.x2");
        int z1 = super.getInt("map.border.z1");
        int z2 = super.getInt("map.border.z2");

        this.borderUpperX = Math.max(x1, x2);
        this.borderLowerX = Math.min(x1, x2);
        this.borderUpperZ = Math.max(z1, z2);
        this.borderLowerZ = Math.min(z1, z2);
    }

    @Override
    public void load() {
        super.load();
        super.getWorld().setDifficulty(Difficulty.NORMAL);
        this.loadShops();
        this.loadTeams();
        this.loadSpawner();

    }

    private void loadSpawner() {
        for (ResourceType type : ResourceType.values()) {
            String key = type.name().toLowerCase();
            List<Location> locations = super.getLocationList("spawner." + key);
            spawnerLocations.put(type, locations);
        }
    }

    private void loadTeams() {
        for (GameTeam team : TeamManager.getTeams()) {
            String key = "teams." + team.getColor().name().toLowerCase();
            Location spawn = super.getLocation(key + ".spawn");
            if (spawn == null) {
                throw new IllegalArgumentException("The spawn location for team " + key + " is not set");
            }
            team.setSpawn(spawn);
            team.setBed(new TeamBed(team, super.getLocation(key + ".bed"), super.getEnum(key + ".bed.direction", BlockFace.class)));
        }
    }

    private void loadShops() {
        for (Location location : this.getLocationList("shops")) {
            FakeEntityManager.spawn(new FakeDealer(location));
        }
    }
}
