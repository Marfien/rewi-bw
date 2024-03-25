package dev.marfien.rewibw.game.lobby;

import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.fakeentities.FakeEntityManager;
import dev.marfien.rewibw.game.GameState;
import dev.marfien.rewibw.game.lobby.listeners.LobbyWorldListener;
import dev.marfien.rewibw.game.lobby.listeners.PlayerConnectionListener;
import dev.marfien.rewibw.game.lobby.listeners.PlayerListener;
import dev.marfien.rewibw.perk.PerkManager;
import dev.marfien.rewibw.team.TeamManager;
import dev.marfien.rewibw.shared.usable.ConsumeType;
import dev.marfien.rewibw.shared.usable.UsableItemInfo;
import dev.marfien.rewibw.shared.usable.UsableItemManager;
import dev.marfien.rewibw.util.CpsTester;
import dev.marfien.rewibw.util.FileUtils;
import dev.marfien.rewibw.util.Items;
import dev.marfien.rewibw.voting.MapVoting;
import dev.marfien.rewibw.world.GameWorld;
import dev.marfien.rewibw.world.MapPool;
import lombok.Getter;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

@Getter
public class LobbyGameState extends GameState {

    private static final String LOBBY_WORLD_NAME = "lobby";

    @Getter
    private static LobbyGameState instance;

    private final GameWorld world;
    private final UsableItemManager itemManager = new UsableItemManager();
    private final LobbyCountdown countdown = new LobbyCountdown();

    private final ArrayList<Listener> listeners = new ArrayList<>();

    public LobbyGameState(String lobbyPath) throws IOException {
        if (instance != null) throw new IllegalStateException("LobbyGameState already exists");

        FileUtils.copyFolder(Paths.get(lobbyPath), MapPool.getBukkitWorldContainer().resolve(LOBBY_WORLD_NAME));
        this.world = new GameWorld(LOBBY_WORLD_NAME);
        this.world.load();

        this.listeners.add(new PlayerListener());
        this.listeners.add(new LobbyWorldListener(this.world.getSpawn()));
        this.listeners.add(new PlayerConnectionListener());

        this.itemManager.putHandler(Items.VOTE_ITEM, new UsableItemInfo(ConsumeType.NONE, event -> MapVoting.openGui(event.getPlayer())));
        this.itemManager.putHandler(Items.PERKS_ITEM, new UsableItemInfo(ConsumeType.NONE, event -> PerkManager.openGui(event.getPlayer())));
        instance = this;
    }

    public Listener[] getListeners() {
        return this.listeners.toArray(new Listener[0]);
    }

    @Override
    public void onStart() {
        this.itemManager.register(RewiBWPlugin.getInstance());
        this.countdown.startIdle();
        this.world.load();
        FakeEntityManager.spawn(new CpsTester(this.world.getLocation("cps")));
        this.listeners.addAll(TeamManager.init(this.world));
        PerkManager.init(RewiBWPlugin.getInstance());
    }

    @Override
    public void onStop() {
        this.itemManager.shutdown();
        TeamManager.assignTeams();
    }

}
