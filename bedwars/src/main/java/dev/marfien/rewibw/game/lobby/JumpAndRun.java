package dev.marfien.rewibw.game.lobby;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.CircleEffect;
import de.slikey.effectlib.util.ParticleEffect;
import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.util.Items;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class JumpAndRun {

    private static final Map<Player, Long> records = new HashMap<>();
    private static final Map<Player, Long> startTime = new ConcurrentHashMap<>();

    private static JumpAndRunListener listener;
    private static BukkitTask actionbarBroadcaster;
    private static Effect startEffect;
    private static Effect finishEffect;

    public static void init(LobbyWorld lobbyWorld) {
        listener = new JumpAndRunListener(
                lobbyWorld.asLocation(config -> config.getJumpAndRun().getStart()),
                lobbyWorld.asLocation(config -> config.getJumpAndRun().getFinish())
        );
        Bukkit.getPluginManager().registerEvents(listener, RewiBWPlugin.getInstance());

        startEffect = startCirclePositionEffect(listener.start);
        finishEffect = startCirclePositionEffect(listener.finish);

        // 2 is actionbar
        PacketPlayOutChat packet = new PacketPlayOutChat(null, (byte) 2);
        packet.components = new BaseComponent[]{ new TextComponent() };
        actionbarBroadcaster = Bukkit.getScheduler().runTaskTimerAsynchronously(RewiBWPlugin.getInstance(), () -> {
            long now = System.currentTimeMillis();
            for (Map.Entry<Player, Long> startTimeByPlayer : startTime.entrySet()) {
                Player player = startTimeByPlayer.getKey();
                long startTime = startTimeByPlayer.getValue();

                ((TextComponent) packet.components[0]).setText(ChatColor.DARK_AQUA + formatTime(now - startTime));
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }, 0, 1);
    }

    public static void destroy() {
        if (listener == null) return;

        HandlerList.unregisterAll(listener);
        startEffect.cancel();
        finishEffect.cancel();
        actionbarBroadcaster.cancel();
    }

    private static Effect startCirclePositionEffect(Location location) {
        CircleEffect effect = new CircleEffect(RewiBWPlugin.getEffectManager()) {
            @Override
            protected void display(ParticleEffect particle, Location location, float speed, int amount) {
                super.display(particle, location, speed, 1);
            }
        };
        effect.setLocation(location);
        effect.radius = (float) Math.sqrt(2) / 2;
        effect.enableRotation = false;
        effect.type = EffectType.REPEATING;
        effect.iterations = -1;
        effect.period = 1;
        effect.particles = 30;
        effect.start();
        return effect;
    }

    public static void start(Player player) {
        startTime.put(player, System.currentTimeMillis());
        player.getInventory().setItem(2, Items.JUMP_AND_RUN_RESET_ITEM);
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
    }

    public static void finish(Player player) {
        Long started = startTime.remove(player);
        if (started == null) return;

        long time = System.currentTimeMillis() - started;
        Long previousRecord = records.get(player);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        player.sendMessage("§8[§3JumpAndRun§8] " + Message.JUMP_AND_RUN_FINISH.format(formatTime(time)));
        if (previousRecord == null || time < previousRecord) {
            player.sendMessage("§8[§3JumpAndRun§8] " + Message.JUMP_AND_RUN_NEW_RECORD.format(formatTime(time)));
            records.put(player, time);
        }
    }

    public static void reset(Player player) {
        records.remove(player);
        startTime.remove(player);
    }

    @RequiredArgsConstructor
    private static class JumpAndRunListener implements Listener {

        private final Location start;
        private final Location finish;

        @EventHandler
        private void onJumpAndRunStart(PlayerMoveEvent event) {
            if (event.getTo().distanceSquared(this.start) < 0.5) {
                start(event.getPlayer());
            }
        }

        @EventHandler
        private void onJumpAndRunFinish(PlayerMoveEvent event) {
            if (event.getTo().distanceSquared(this.finish) < 0.5) {
                finish(event.getPlayer());
            }
        }

        @EventHandler
        private void onQuit(PlayerQuitEvent event) {
            reset(event.getPlayer());
        }

    }

    private static String formatTime(long deltaTime) {
        return String.format("%02.2fs", deltaTime / 1000f);
    }

}
