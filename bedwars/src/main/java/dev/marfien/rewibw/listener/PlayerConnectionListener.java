package dev.marfien.rewibw.listener;

import com.google.gson.JsonObject;
import dev.marfien.rewibw.PlayerManager;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.GameStateManager;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.spigotmc.SpigotConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerConnectionListener implements Listener {

    private static final PacketPlayOutPlayerListHeaderFooter TABLIST_PACKET = new PacketPlayOutPlayerListHeaderFooter();

    static {
        IChatBaseComponent header = CraftChatMessage.fromString("§eRewi§6§lBW\n§7Du spielst auf §6BedWars-" + ThreadLocalRandom.current().nextInt(20), true)[0];
        IChatBaseComponent  footer = CraftChatMessage.fromString("§7Lust auf tolle Vorteile und Features?\n§6Schreib mir auf Discord (marfien) oder an hello@marfien.dev!",
                true)[0];
        PacketDataSerializer serializer = new PacketDataSerializer(Unpooled.buffer());
        try {
            serializer.a(header);
            serializer.a(footer);
            TABLIST_PACKET.a(serializer);
        } catch (IOException e) {
            RewiBWPlugin.getPluginLogger().error("Error while creating tablist packet", e);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerManager.resetPlayerStatus(player);

        if (RewiBWPlugin.getPluginConfig().getServer().isTablistHeaderFooter()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(TABLIST_PACKET);
        }
    }

    @EventHandler
    private void onServerPing(ServerListPingEvent event) {
        event.setMaxPlayers(RewiBWPlugin.getPluginConfig().getTeams().getMaxPlayers());
        Boolean serializeGameData = RewiBWPlugin.getPluginConfig().getServer().getSerializeGameInfoInMotd();

        if (serializeGameData == Boolean.TRUE || serializeGameData == null && SpigotConfig.bungee) {
            event.setMotd(serializeGameData());
        } else {
            event.setMotd(
                    "§eRewi§6§lBW §8- §7" + GameStateManager.getActiveGameState().getName() + "\n" +
                    "§7" + GameStateManager.getActiveGameState().getMotdInfo()
            );
        }
    }

    private static String serializeGameData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("activePlayers", Bukkit.getOnlinePlayers().size() - PlayerManager.getSpectatorCount());
        jsonObject.addProperty("state", GameStateManager.getActiveGameState().getName());
        jsonObject.addProperty("gameId", RewiBWPlugin.GAME_ID);
        jsonObject.addProperty("additionalInfo", GameStateManager.getActiveGameState().getMotdInfo());

        return Base64.getEncoder().encodeToString(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
    }

}
