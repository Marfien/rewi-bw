package dev.marfien.rewibw.game.end;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.PlayerManager;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.GameState;
import dev.marfien.rewibw.game.lobby.LobbyGameState;
import dev.marfien.rewibw.game.lobby.LobbyWorld;
import dev.marfien.rewibw.game.lobby.listeners.LobbyWorldListener;
import dev.marfien.rewibw.game.lobby.listeners.PlayerListener;
import dev.marfien.rewibw.shared.config.LobbyConfig;
import dev.marfien.rewibw.team.GameTeam;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;

import java.io.IOException;
import java.util.logging.Logger;

@Getter
public class EndGameState extends GameState {

    private static final Logger LOGGER = RewiBWPlugin.getPluginLogger();

    private final LobbyWorld lobby;

    //private final RadioSongPlayer songPlayer = new RadioSongPlayer(NBSDecoder.parse(RewiBWPlugin.getInstance().getResource("end.nbs")));
    private final EndCountdown countdown = new EndCountdown(this);
    private final GameTeam winner;

    @Getter
    private final Listener[] listeners;

    @SneakyThrows
    public EndGameState(GameTeam winner) {
        this.winner = winner;
        this.lobby = LobbyWorld.setupLobby(RewiBWPlugin.getPluginConfig().getLobbyMap());
        this.lobby.load();

        this.listeners = new Listener[]{
                new LobbyWorldListener(),
                new PlayerListener(this.lobby.asLocation(LobbyConfig::getSpawn)),
                new ChatFormatListener()
        };
    }

    @Override
    public void onStart() {
        Location spawn = this.lobby.asLocation(LobbyConfig::getSpawn);
        LOGGER.info("Teleporting players to " + spawn);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(spawn);
            PlayerManager.showSpectators(player);
            //this.songPlayer.addPlayer(player);
        }

//        this.songPlayer.setAutoDestroy(true);
//        this.songPlayer.setPlaying(true);

        this.countdown.start();
        LOGGER.info("Winner: " + this.winner);
        if (this.winner == null) {
            Message.broadcast(" ");
            Message.broadcast(Message.NO_WINNER.toString());
            Message.broadcast(" ");
            return;
        }

        Message.broadcast(" ");
        Message.broadcast(Message.BROADCAST_WINNER.format(this.winner.getColor().getDisplayName()));
        Message.broadcast(" ");

        Location location = this.lobby.asLocation(config -> config.getTeams().get(this.winner.getColor()).getJoiner());
        Bukkit.getScheduler().runTaskLater(RewiBWPlugin.getInstance(), () -> {
            //this.songPlayer.setPlaying(false);
            Firework firework = this.lobby.getWorld().spawn(location, Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.setPower(10);
            meta.addEffect(
                    FireworkEffect.builder()
                            .with(FireworkEffect.Type.BURST)
                            .withTrail()
                            .withColor(this.winner.getColor().getDyeColor().getColor())
                            .build()
            );
            firework.setFireworkMeta(meta);
        }, 70L);
    }

    @Override
    public void onStop() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("§cDas Spiel ist vorbei.");
        }
        Bukkit.shutdown();
    }
}
