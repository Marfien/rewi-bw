package dev.marfien.rewibw.world;

import dev.marfien.rewibw.shared.world.GameWorldGenerator;
import dev.marfien.rewibw.util.Position;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import java.util.*;

@Getter
public class GameWorld {

    public static final String CONFIG_FILE = "config.yaml";

    private World world;

    private final String name;

    private final YamlConfiguration configuration;

    private Location spawn;

    public GameWorld(String name) {
        this.name = name;
        this.configuration = YamlConfiguration.loadConfiguration(Bukkit.getWorldContainer().toPath().resolve(this.name).resolve(CONFIG_FILE).toFile());
    }

    public void load() {
        this.world = WorldCreator.name(this.name)
                .generator(Optional.ofNullable(this.getEnum("world.generator", GameWorldGenerator.class)).map(GameWorldGenerator::getGenerator).orElse(null))
                .environment(this.getEnum("world.environment", World.Environment.class, World.Environment.NORMAL))
                .createWorld();
        this.world.setKeepSpawnInMemory(false);
        this.world.setSpawnFlags(false, false);

        if (this.isLong("world.init_time")) {
            this.world.setTime(this.getLong("world.init_time"));
        }

        if (this.isEnum("world.difficulty", Difficulty.class)) {
            this.world.setDifficulty(this.getEnum("world.difficulty", Difficulty.class));
        }

        this.spawn = this.getLocation("spawn");
    }

    public Location getSpawn() {
        return this.spawn.clone();
    }

    // Config shit below here

    public Set<String> getKeys(boolean deep) {
        return this.configuration.getKeys(deep);
    }

    public Map<String, Object> getValues(boolean deep) {
        return this.configuration.getValues(deep);
    }

    public boolean contains(String path) {
        return this.configuration.contains(path);
    }

    public boolean isSet(String path) {
        return this.configuration.isSet(path);
    }

    public String getCurrentPath() {
        return this.configuration.getCurrentPath();
    }

    public String getName() {
        return this.configuration.getName();
    }

    public Configuration getRoot() {
        return this.configuration.getRoot();
    }

    public ConfigurationSection getParent() {
        return this.configuration.getParent();
    }

    public Object get(String path) {
        return this.configuration.get(path);
    }

    public Object get(String path, Object defaultValue) {
        return this.configuration.get(path, defaultValue);
    }

    public String getString(String path) {
        return this.configuration.getString(path);
    }

    public String getString(String path, String defaultValue) {
        return this.configuration.getString(path, defaultValue);
    }

    public boolean isString(String path) {
        return this.configuration.isString(path);
    }

    public int getInt(String path) {
        return this.configuration.getInt(path);
    }

    public int getInt(String path, int defaultValue) {
        return this.configuration.getInt(path, defaultValue);
    }

    public boolean isInt(String path) {
        return this.configuration.isInt(path);
    }

    public boolean getBoolean(String path) {
        return this.configuration.getBoolean(path);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return this.configuration.getBoolean(path, defaultValue);
    }

    public boolean isBoolean(String path) {
        return this.configuration.isBoolean(path);
    }

    public double getDouble(String path) {
        return this.configuration.getDouble(path);
    }

    public double getDouble(String path, double defaultValue) {
        return this.configuration.getDouble(path, defaultValue);
    }

    public boolean isDouble(String path) {
        return this.configuration.isDouble(path);
    }

    public long getLong(String path) {
        return this.configuration.getLong(path);
    }

    public long getLong(String path, long defaultValue) {
        return this.configuration.getLong(path, defaultValue);
    }

    public boolean isLong(String path) {
        return this.configuration.isLong(path);
    }

    public List<?> getList(String path) {
        return this.configuration.getList(path);
    }

    public List<?> getList(String path, List<?> defaultValue) {
        return this.configuration.getList(path, defaultValue);
    }

    public boolean isList(String path) {
        return this.configuration.isList(path);
    }

    public List<String> getStringList(String path) {
        return this.configuration.getStringList(path);
    }

    public List<Integer> getIntegerList(String path) {
        return this.configuration.getIntegerList(path);
    }

    public List<Boolean> getBooleanList(String path) {
        return this.configuration.getBooleanList(path);
    }

    public List<Double> getDoubleList(String path) {
        return this.configuration.getDoubleList(path);
    }

    public List<Float> getFloatList(String path) {
        return this.configuration.getFloatList(path);
    }

    public List<Long> getLongList(String path) {
        return this.configuration.getLongList(path);
    }

    public List<Byte> getByteList(String path) {
        return this.configuration.getByteList(path);
    }

    public List<Character> getCharacterList(String path) {
        return this.configuration.getCharacterList(path);
    }

    public List<Short> getShortList(String path) {
        return this.configuration.getShortList(path);
    }

    public List<Map<?, ?>> getMapList(String path) {
        return this.configuration.getMapList(path);
    }

    public Vector getVector(String path) {
        return this.getVector(path, null);
    }

    public Vector getVector(String path, Vector defaultValue) {
        return this.isVector(path) ? new Vector(
                this.getDouble(path + ".x"),
                this.getDouble(path + ".y"),
                this.getDouble(path + ".z")
        ) : defaultValue;

    }

    public boolean isVector(String path) {
        return this.isDouble(path + ".x")
                && this.isDouble(path + ".y")
                && this.isDouble(path + ".z");
    }

    public Position getPosition(String path) {
        return this.getPosition(path, null);
    }

    public Position getPosition(String path, Position defaultValue) {
        return this.isPosition(path) ? new Position(
                this.configuration.getDouble(path + ".x"),
                this.configuration.getDouble(path + ".y"),
                this.configuration.getDouble(path + ".z"),
                (float) this.configuration.getDouble(path + ".yaw"),
                (float) this.configuration.getDouble(path + ".pitch")
        ) : defaultValue;
    }

    public boolean isPosition(String path) {
        return this.isVector(path);
    }

    public boolean isLocation(String path) {
        return this.isPosition(path);
    }

    public Location getLocation(String path) {
        return this.getLocation(path, null);
    }

    public Location getLocation(String path, Location defaultValue) {
        return Optional.ofNullable(this.getPosition(path)).map(pos -> pos.toLocation(this.world)).orElse(defaultValue);
    }

    public List<Location> getLocationList(String path) {
        return getLocationList(path, null);
    }

    public List<Location> getLocationList(String path, List<Location> defaultValue) {
        List<?> list = this.getList(path);
        if (list == null) return defaultValue;

        List<Location> result = new ArrayList<>(list.size());
        if (list.isEmpty()) return defaultValue;

        for (Object o : list) {
            if (!(o instanceof Map)) continue;
            Map map = (Map) o;

            if (!map.containsKey("x") || !map.containsKey("y") || !map.containsKey("z")) continue;

            result.add(new Location(
                    this.world,
                    ((Number) map.get("x")).doubleValue(),
                    ((Number) map.get("y")).doubleValue(),
                    ((Number) map.get("z")).doubleValue(),
                    ((Number) map.getOrDefault("yaw", 0F)).floatValue(),
                    ((Number) map.getOrDefault("pitch", 0F)).floatValue()
            ));
        }

        return result;
    }

    public Color getColor(String path) {
        return this.getColor(path, null);
    }

    public Color getColor(String path, Color defaultValue) {
        return this.isColor(path)
                ? Color.fromRGB(this.getInt(path))
                : defaultValue;
    }

    public boolean isColor(String path) {
        return this.isInt(path);
    }

    public <T extends Enum<T>> T getEnum(String path, Class<T> enumClass, T defaultValue) {
        if (!this.isString(path)) return defaultValue;
        try {
            return Enum.valueOf(enumClass, this.getString(path).toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return defaultValue;
        }
    }

    public <T extends Enum<T>> T getEnum(String path, Class<T> enumClass) {
        return this.getEnum(path, enumClass, null);
    }

    public <T extends Enum<T>> boolean isEnum(String path, Class<T> enumClass) {
        return this.getEnum(path, enumClass) != null;
    }

    public ConfigurationSection getConfigurationSection(String path) {
        return this.configuration.getConfigurationSection(path);
    }

    public boolean isConfigurationSection(String path) {
        return this.configuration.isConfigurationSection(path);
    }
}
