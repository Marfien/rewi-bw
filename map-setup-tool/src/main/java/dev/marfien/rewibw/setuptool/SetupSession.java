package dev.marfien.rewibw.setuptool;

import dev.marfien.rewibw.shared.config.ConfigLoader;
import dev.marfien.rewibw.shared.config.MapConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.World;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.nio.file.Path;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class SetupSession {

    private final UUID owner;
    private final World world;
    private final MapConfig mapConfig = new MapConfig();

    public void save(Path path) throws ConfigurateException {
        ConfigurationLoader<?> loader = ConfigLoader.loadConfigIn(path);
        ConfigurationNode node = loader.createNode();
        node.set(this.mapConfig);
        loader.save(node);
    }

}
