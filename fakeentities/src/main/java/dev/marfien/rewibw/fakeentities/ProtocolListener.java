package dev.marfien.rewibw.fakeentities;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ProtocolListener implements Listener {

    private static final String DECODER_NAME = "rewibw-decoder";

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        inject(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        eject(event.getPlayer());
    }

    public void inject(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        channel.pipeline().addAfter("decoder", DECODER_NAME, new MessageToMessageDecoder<Packet<?>>() {
            @Override
            protected void decode(ChannelHandlerContext context, Packet<?> packet, List<Object> out) throws IOException {
                if (readPacket(player, packet)) {
                    out.add(packet);
                }
            }
        });
    }

    public void eject(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        ChannelPipeline pipeline = channel.pipeline();
        if (pipeline.get(DECODER_NAME) != null) {
            channel.pipeline().remove(DECODER_NAME);
        }
    }

    private boolean readPacket(Player player, Packet<?> packetIn) throws IOException {
        if (!(packetIn instanceof PacketPlayInUseEntity)) return true;

        PacketDataSerializer serializer = new PacketDataSerializer(Unpooled.buffer());
        packetIn.b(serializer);

        int entityId = serializer.e();
        Optional<FakeEntity> entity = FakeEntityManager.getFakeEntity(entityId);

        if (!entity.isPresent()) return true;
        FakeEntity fakeEntity = entity.get();

        PacketPlayInUseEntity.EnumEntityUseAction action = serializer.a(PacketPlayInUseEntity.EnumEntityUseAction.class);

        if (action == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
            fakeEntity.onAttack(player);
        }
        if (action == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT) {
            fakeEntity.onInteract(player);
        }

        return false;
    }

}
