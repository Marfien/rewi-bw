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
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Getter
public class GameMap extends GameWorld {

    private final GameMapInfo mapInfo;

    private final Map<ResourceType, Collection<Location>> spawnerLocations = new EnumMap<>(ResourceType.class);

    private final int borderX1;
    private final int borderX2;
    private final int borderZ1;
    private final int borderZ2;

    public GameMap(String name, GameMapInfo info) {
        super(name);
        this.mapInfo = info;
        this.borderX1 = super.getInt("border.x1");
        this.borderX2 = super.getInt("border.x2");
        this.borderZ1 = super.getInt("border.z1");
        this.borderZ2 = super.getInt("border.z2");
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
