package dev.marfien.rewibw.game.end;

import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.PlayerManager;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.GameState;
import dev.marfien.rewibw.game.lobby.LobbyGameState;
import dev.marfien.rewibw.game.lobby.listeners.LobbyWorldListener;
import dev.marfien.rewibw.game.lobby.listeners.PlayerListener;
import dev.marfien.rewibw.team.GameTeam;
import dev.marfien.rewibw.world.GameWorld;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;

@RequiredArgsConstructor
public class EndGameState extends GameState {

    private static final GameWorld lobby = LobbyGameState.getInstance().getWorld();

    // TODO find a good song
    private final RadioSongPlayer songPlayer = new RadioSongPlayer(NBSDecoder.parse(RewiBWPlugin.getInstance().getResource("JohnCena.nbs")));
    private final EndCountdown countdown = new EndCountdown(this);
    private final GameTeam winner;

    @Getter
    private final Listener[] listeners = new Listener[]{
            new LobbyWorldListener(lobby.getSpawn()),
            new PlayerListener(),
            new ChatFormatListener()
    };

    @Override
    public void onStart() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(lobby.getSpawn());
            PlayerManager.showSpectators(player);
            this.songPlayer.addPlayer(player);
        }

        this.songPlayer.setAutoDestroy(true);
        this.songPlayer.setPlaying(true);

        this.countdown.start();
        if (winner == null) {
            Message.broadcast(" ");
            Message.broadcast(Message.NO_WINNER.toString());
            Message.broadcast(" ");
            return;
        }

        Message.broadcast(" ");
        Message.broadcast(Message.BROADCAST_WINNER.format(winner.getColor().getDisplayName()));
        Message.broadcast(" ");

        Location location = lobby.getLocation("teams." + winner.getColor().name().toLowerCase() + ".joiner");
        Bukkit.getScheduler().runTaskLater(RewiBWPlugin.getInstance(), () -> {
            this.songPlayer.setPlaying(false);
            Firework firework = lobby.getWorld().spawn(location, Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.setPower(10);
            meta.addEffect(
                    FireworkEffect.builder()
                            .with(FireworkEffect.Type.BURST)
                            .withTrail()
                            .withColor(winner.getColor().getDyeColor().getColor())
                            .build()
            );
            firework.setFireworkMeta(meta);
        }, 70L);
    }

    @Override
    public void onStop() {
        // kick all plyers
        // stop server
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("§cDas Spiel ist vorbei.");
        }
        Bukkit.shutdown();
    }
}
