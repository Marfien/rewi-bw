package dev.marfien.rewibw.world;

import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.shared.FileUtils;
import dev.marfien.rewibw.shared.config.MapConfig;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapPool {

    private static final Logger LOGGER = RewiBWPlugin.getPluginLogger();

    @Getter
    private static final Path bukkitWorldContainer = Bukkit.getWorldContainer().toPath();
    private static final Map<String, Path> maps = new ConcurrentHashMap<>();
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
        if (!maps.containsKey(name)) {
            LOGGER.debug("Loading map {} from {}", name, path);
            maps.put(name, path);
            return;
        }

        switch (policy) {
            case THROW_EXCEPTION:
                throw new IllegalArgumentException("Map with name " + name + " already exists");
            case WARN:
                LOGGER.warn("Map with name {} already exists", name);
                break;
            case REPLACE:
                LOGGER.debug("Replacing map {} from {}", name, path);
                maps.put(name, path);
                break;
            case IGNORE:
                break;
        }
    }

    public static MapWorld requestMap(String name) throws IOException {
        MapWorld map = requestedMaps.get(name);
        if (map != null) return map;

        Path sourcePath = maps.get(name);
        if (sourcePath == null)  {
            throw new IllegalArgumentException("Map with name '" + name + "' is unknown");
        }
        Path targetPath = bukkitWorldContainer.resolve(name);

        LOGGER.debug("Copying requested map {} to {}", name, targetPath);
        FileUtils.copyFolder(sourcePath, targetPath);
        map = new MapWorld(name, MapConfig.loader(targetPath).load().require(MapConfig.class));
        requestedMaps.put(name, map);

        return map;
    }

    public static Collection<String> getMapNames() {
        return maps.keySet();
    }

    public static boolean contains(String name) {
        return maps.containsKey(name);
    }

    public static Path getSourcePath(String name) {
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
