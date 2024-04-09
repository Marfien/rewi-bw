package dev.marfien.rewibw.game.lobby;

import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.shared.FileUtils;
import dev.marfien.rewibw.shared.config.ConfigLoader;
import dev.marfien.rewibw.shared.config.LobbyConfig;
import dev.marfien.rewibw.world.GameWorld;
import dev.marfien.rewibw.world.MapPool;

import java.io.IOException;
import java.nio.file.Path;

public class LobbyWorld extends GameWorld<LobbyConfig> {

    private static LobbyWorld instance;

    public static LobbyWorld getInstance() {
        if (instance == null) {
            throw new IllegalStateException("LobbyWorld has not been initialized yet");
        }

        return instance;
    }

    private LobbyWorld(String name, LobbyConfig config) {
        super(name, config);
        if (instance != null) {
            throw new IllegalStateException("LobbyWorld has already been initialized");
        }
        instance = this;
    }

    static LobbyWorld setupLobby(Path lobbyPath) throws IOException {
        Path lobbyWorldFolder = MapPool.getBukkitWorldContainer().resolve("lobby");
        FileUtils.copyFolder(lobbyPath, lobbyWorldFolder);
        RewiBWPlugin.getPluginLogger().info("Copied lobby world to {}", lobbyWorldFolder);
        LobbyConfig config = ConfigLoader.loadConfigIn(lobbyWorldFolder).load().require(LobbyConfig.class);

        if (config == null)
            throw new IllegalStateException("No configuration file in lobby world found. Did you define the right lobby path?");

        String name = lobbyWorldFolder.getFileName().toString();
        return new LobbyWorld(name, config);
    }

}
