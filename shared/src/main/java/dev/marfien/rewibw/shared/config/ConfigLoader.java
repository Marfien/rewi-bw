package dev.marfien.rewibw.shared.config;

import lombok.experimental.UtilityClass;
import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

@UtilityClass
public class ConfigLoader {

    public static ConfigurationLoader<? extends ConfigurationNode> loadConfigIn(Path folder) {
        return YamlConfigurationLoader.builder()
                .path(folder.resolve("config.yaml"))
                .build();
    }

    public static ConfigurationLoader<? extends ConfigurationNode> loadPluginConfig(Plugin plugin) {
        return loadConfigIn(plugin.getDataFolder().toPath());
    }

}
