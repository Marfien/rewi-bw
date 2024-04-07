package dev.marfien.rewibw.game.lobby;

import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.command.ForceMapCommand;
import dev.marfien.rewibw.command.StartCommand;
import dev.marfien.rewibw.fakeentities.FakeEntityManager;
import dev.marfien.rewibw.game.GameState;
import dev.marfien.rewibw.game.lobby.listeners.LobbyWorldListener;
import dev.marfien.rewibw.game.lobby.listeners.PlayerConnectionListener;
import dev.marfien.rewibw.game.lobby.listeners.PlayerListener;
import dev.marfien.rewibw.game.lobby.listeners.TeamJoinerListener;
import dev.marfien.rewibw.perk.PerkGroup;
import dev.marfien.rewibw.perk.PerkManager;
import dev.marfien.rewibw.shared.Position;
import dev.marfien.rewibw.shared.config.LobbyConfig;
import dev.marfien.rewibw.RewiBWConfig;
import dev.marfien.rewibw.shared.usable.ConsumeType;
import dev.marfien.rewibw.shared.usable.UsableItemInfo;
import dev.marfien.rewibw.shared.usable.UsableItemManager;
import dev.marfien.rewibw.team.TeamManager;
import dev.marfien.rewibw.util.Items;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.nio.file.Path;

@Getter
public class LobbyGameState extends GameState {

    private final LobbyWorld world;
    private final MapVoting mapVoting;
    private final UsableItemManager itemManager = new UsableItemManager();
    private final LobbyCountdown countdown;

    private final Listener[] listeners;
    private final TeamJoinerListener teamJoinerListener = new TeamJoinerListener();

    public LobbyGameState(Path lobbyPath, RewiBWConfig.VoteConfig voteConfig) throws IOException {
        this.mapVoting = new MapVoting(voteConfig);
        this.countdown = new LobbyCountdown(this.mapVoting);

        this.world = LobbyWorld.setupLobby(lobbyPath);
        this.world.load();

        this.listeners = new Listener[]{
                new PlayerListener(this.world.asLocation(LobbyConfig::getSpawn)),
                new LobbyWorldListener(),
                new PlayerConnectionListener(this.countdown, this.mapVoting),
                this.teamJoinerListener
        };

        this.itemManager.putHandler(Items.VOTE_ITEM, new UsableItemInfo(ConsumeType.NONE, event -> this.mapVoting.openGui(event.getPlayer())));
        this.itemManager.putHandler(Items.PERKS_ITEM, new UsableItemInfo(ConsumeType.NONE, event -> PerkManager.openGui(event.getPlayer())));
    }

    @Override
    public void onStart() {
        Bukkit.unloadWorld("world", false);
        this.itemManager.register(RewiBWPlugin.getInstance());
        this.countdown.startIdle();
        this.world.load();

        TeamManager.initTeams(this.world, this.teamJoinerListener);

        PerkManager.init(RewiBWPlugin.getInstance());

        Position position = this.world.getConfig().getCpsTester();
        if (position == null) return;

        Bukkit.getPluginCommand("start").setExecutor(new StartCommand());
        Bukkit.getPluginCommand("forcemap").setExecutor(new ForceMapCommand(this.mapVoting));

        FakeEntityManager.spawn(new CpsTester(position.toLocation(this.world.getWorld())));
    }

    @Override
    public void onStop() {
        this.itemManager.shutdown();
        TeamManager.assignTeams();
        this.mapVoting.destroyGui();
        // TODO move this into PlayingGameState#onStart
        Bukkit.getScheduler().runTaskLaterAsynchronously(RewiBWPlugin.getInstance(), this.world::unload, 20);
        for (PerkGroup<?> perkGroup : PerkManager.getPerkGroups()) {
            perkGroup.destroyGui();
        }
    }

}
