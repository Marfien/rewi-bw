package dev.marfien.rewibw.game.lobby;

import dev.marfien.rewibw.RewiBWConfig;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.command.ForceMapCommand;
import dev.marfien.rewibw.command.StartCommand;
import dev.marfien.rewibw.fakeentities.FakeEntityManager;
import dev.marfien.rewibw.game.GameState;
import dev.marfien.rewibw.game.lobby.listeners.*;
import dev.marfien.rewibw.perk.PerkGroup;
import dev.marfien.rewibw.perk.PerkManager;
import dev.marfien.rewibw.shared.Position;
import dev.marfien.rewibw.shared.config.LobbyConfig;
import dev.marfien.rewibw.shared.usable.ConsumeType;
import dev.marfien.rewibw.shared.usable.UsableItemInfo;
import dev.marfien.rewibw.shared.usable.UsableItemManager;
import dev.marfien.rewibw.team.TeamManager;
import dev.marfien.rewibw.util.Items;
import dev.marfien.rewibw.world.MapWorld;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
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
        super(null);
        this.mapVoting = new MapVoting(voteConfig);
        this.countdown = new LobbyCountdown(this.mapVoting);

        this.world = LobbyWorld.setupLobby(lobbyPath);
        this.world.load();

        this.listeners = new Listener[]{
                new PlayerListener(this.world.asLocation(LobbyConfig::getSpawn)),
                new LobbyWorldListener(),
                new PlayerConnectionListener(this.countdown, this.mapVoting),
                new CatchListener(),
                this.teamJoinerListener
        };

        this.itemManager.putHandler(Items.VOTE_ITEM, new UsableItemInfo(ConsumeType.NONE, event -> this.mapVoting.openGui(event.getPlayer())));
        this.itemManager.putHandler(Items.JUMP_AND_RUN_RESET_ITEM, new UsableItemInfo(ConsumeType.NONE,
                event -> event.getPlayer().teleport(this.world.asLocation(config -> config.getJumpAndRun().getStart()))));
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

        this.world.getConfig().getCpsTester().ifPresent(position -> {
            FakeEntityManager.spawn(new CpsTester(position.toLocation(this.world.getWorld())));
        });

        if (this.world.getConfig().getJumpAndRun() != null) {
            JumpAndRun.init(this.world);
        }

        Bukkit.getPluginCommand("start").setExecutor(new StartCommand());
        Bukkit.getPluginCommand("forcemap").setExecutor(new ForceMapCommand(this.mapVoting));

    }

    @Override
    public void onStop() {
        JumpAndRun.destroy();
        this.itemManager.shutdown();
        TeamManager.assignTeams();
        this.mapVoting.destroyGui();
        // TODO move this into PlayingGameState#onStart
        RewiBWPlugin.getScheduler().runTaskLater(() -> {
            for (Chunk loadedChunk : this.world.getWorld().getLoadedChunks()) {
                loadedChunk.unload(true, true);
            }
        }, 20);
        for (PerkGroup<?> perkGroup : PerkManager.getPerkGroups()) {
            perkGroup.destroyGui();
        }
    }

    @Override
    public String getMotdInfo() {
        MapWorld winner = this.mapVoting.getWinner();
        RewiBWConfig.TeamConfig teamConfig = RewiBWPlugin.getPluginConfig().getTeams();
        return String.format("%dx%d - %s",
                teamConfig.getVariants().length,
                teamConfig.getPlayersPerTeam(),
                winner == null ? "Votephase" : winner.getName()
        );
    }

    @Override
    public String getName() {
        if (Bukkit.getOnlinePlayers().size() >= RewiBWPlugin.getPluginConfig().getTeams().getMaxPlayers())
            return ChatColor.GOLD + "Lobby";
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            return ChatColor.BLACK + "Lobby";
        } else {
            return ChatColor.GREEN + "Lobby";
        }
    }
}
