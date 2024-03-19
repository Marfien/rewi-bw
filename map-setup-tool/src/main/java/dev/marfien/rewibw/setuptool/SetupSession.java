package dev.marfien.rewibw.setuptool;

import dev.marfien.rewibw.shared.TeamColor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public class SetupSession {

    private final World world;
    private final String displayName;
    private final String displayItem;

    private final Collection<Location> bronzeSpawns = new HashSet<>();
    private final Collection<Location> silverSpawns = new HashSet<>();
    private final Collection<Location> goldSpawns = new HashSet<>();
    private final Collection<Location> shops = new HashSet<>();

    private final Map<TeamColor, TeamInfo> teams = new EnumMap<>(TeamColor.class);

    private Location spawn;

    public void save(Path path) throws IOException {
        YamlConfiguration config = new YamlConfiguration();
        config.set("spawn", serializeLocation(spawn));
        config.set("world.generator", "EMPTY");
        config.set("map.displayName", displayName);
        config.set("map.icon", displayItem);

        config.set("shops", shops.stream().map(SetupSession::serializeLocation).collect(Collectors.toList()));
        config.set("spawner.bronze", bronzeSpawns.stream().map(SetupSession::serializeLocation).collect(Collectors.toList()));
        config.set("spawner.silver", silverSpawns.stream().map(SetupSession::serializeLocation).collect(Collectors.toList()));
        config.set("spawner.gold", goldSpawns.stream().map(SetupSession::serializeLocation).collect(Collectors.toList()));

        for (Map.Entry<TeamColor, TeamInfo> entry : teams.entrySet()) {
            String key = "teams." + entry.getKey().name().toLowerCase();
            TeamInfo info = entry.getValue();
            config.set(key + ".spawn", serializeLocation(info.spawn));
            config.set(key + ".bed", serializeLocation(info.bed));
            config.set(key + ".direction", info.direction.name());
        }

        config.save(path.toFile());
    }

    private static Map<String, Object> serializeLocation(Location location) {
        Map<String, Object> map = new HashMap<>();
        map.put("x", location.getX());
        map.put("y", location.getY());
        map.put("z", location.getZ());
        map.put("yaw", location.getYaw());
        map.put("pitch", location.getPitch());
        return map;
    }

    @Setter
    public static class TeamInfo {

        private Location spawn;
        private Location bed;
        private BlockFace direction;

    }

}
