package dev.marfien.rewibw.world;

import dev.marfien.rewibw.shared.FileUtils;
import dev.marfien.rewibw.shared.config.MapConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.spongepowered.configurate.ConfigurateException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapPool {

    @Getter
    private static final Path bukkitWorldContainer = Bukkit.getWorldContainer().toPath();
    private static final Map<String, MapConfig> maps = new ConcurrentHashMap<>();
    private static final Map<String, MapWorld> requestedMaps = new ConcurrentHashMap<>();

    public static void loadMaps(Path baseDir, DuplicatePolicy policy) throws IOException {
        Files.list(baseDir)
                .parallel()
                .filter(Files::isDirectory)
                .filter(path -> Files.exists(path.resolve("config.yaml")))
                .forEach(path -> loadMap(path, policy));
    }

    public static void loadMaps(Path baseDir) throws IOException {
        loadMaps(baseDir, DuplicatePolicy.THROW_EXCEPTION);
    }

    public static void loadMap(Path path, DuplicatePolicy policy) {
        String name = path.getFileName().toString();
        try {
            MapConfig config = MapConfig.loader(path).load().get(MapConfig.class);

            if (!maps.containsKey(name)) {
                maps.put(name, config);
                return;
            }

            switch (policy) {
                case THROW_EXCEPTION:
                    throw new IllegalArgumentException("Map with name " + name + " already exists");
                case WARN:
                    // TODO logging
                    System.err.println("Map with name " + name + " already exists");
                    break;
                case REPLACE:
                    maps.put(name, config);
                    break;
                case IGNORE:
                    break;
            }
        } catch (ConfigurateException e) {
            System.err.println("Could not load map '" + name + "': " + e.getMessage());
        }
    }

    public static MapWorld requestMap(GameMapInfo info) throws IOException {
        MapWorld map = requestedMaps.get(info.getName());
        if (map != null) return map;

        FileUtils.copyFolder(info.getPath(), bukkitWorldContainer.resolve(info.getName()));
        map = new MapWorld(info.getName(), info);
        requestedMaps.put(info.getName(), map);
        return map;
    }

    public static MapWorld requestMap(String name) throws IOException {
        GameMapInfo info = maps.get(name);
        if (info == null) return null;

        return requestMap(info);
    }

    public static Collection<GameMapInfo> getMaps() {
        return maps.values();
    }

    public static Collection<String> getMapNames() {
        return maps.keySet();
    }

    public static GameMapInfo getMapInfo(String name) {
        return maps.get(name);
    }

    public static int size() {
        return maps.size();
    }

    public enum DuplicatePolicy {
        THROW_EXCEPTION,
        WARN,
        REPLACE,
        IGNORE

    }

}
