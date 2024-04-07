package dev.marfien.rewibw.shared.config;

import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

public class ConfigLoader {

    public static ConfigurationLoader<?> loadConfigIn(Path folder) {
        return YamlConfigurationLoader.builder()
                .path(folder.resolve("config.yaml"))
                .build();
    }

    public static ConfigurationLoader<?> loadPluginConfig(Plugin plugin) {
        return loadConfigIn(plugin.getDataFolder().toPath());
    }

}
