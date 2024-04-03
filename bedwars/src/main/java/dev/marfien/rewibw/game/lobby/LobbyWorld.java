package dev.marfien.rewibw.game.lobby;

import dev.marfien.rewibw.shared.FileUtils;
import dev.marfien.rewibw.shared.config.LobbyConfig;
import dev.marfien.rewibw.world.GameWorld;
import dev.marfien.rewibw.world.MapPool;

import java.io.IOException;
import java.nio.file.Path;

public class LobbyWorld extends GameWorld<LobbyConfig> {

    private LobbyWorld(String name, LobbyConfig config) {
        super(name, config);
    }

    public static LobbyWorld setupLobby(Path lobbyPath) throws IOException {
        Path lobbyWorldFolder = MapPool.getBukkitWorldContainer().resolve("lobby");
        FileUtils.copyFolder(lobbyPath, lobbyWorldFolder);
        LobbyConfig config = LobbyConfig.loader(lobbyWorldFolder).load().require(LobbyConfig.class);
        // TODO why is config null
        String name = lobbyWorldFolder.getFileName().toString();
        return new LobbyWorld(name, config);
    }

}
