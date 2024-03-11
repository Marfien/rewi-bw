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

    private final Material icon;
    private final String displayName;
    private final String builder;

    private final Map<ResourceType, Collection<Location>> spawnerLocations = new EnumMap<>(ResourceType.class);

    public GameMap(String name) {
        super(name);
        this.icon = super.getEnum("map.icon", Material.class);
        this.displayName = super.getString("map.displayName");
        this.builder = super.getString("map.builder");
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
            String key = team.getColor().name().toLowerCase();
            Location spawn = super.getLocation("teams." + key + ".spawn");
            if (spawn == null) {
                throw new IllegalArgumentException("The spawn location for team " + key + " is not set");
            }
            team.setSpawn(spawn);
            team.setBed(new TeamBed(team, super.getLocation("teams." + key + ".bed"), super.getEnum("teams." + key + ".bed.direction", BlockFace.class)));
        }
    }

    private void loadShops() {
        for (Location location : this.getLocationList("shops")) {
            FakeEntityManager.spawn(new FakeDealer(location));
        }
    }
}
