package dev.marfien.rewibw.labymod;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.EncoderException;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.nio.charset.Charset;

/**
 * Utility class for sending messages to LabyMod
 * <a href="https://docs.labymod.net/pages/server/protocol/protocol/">LabyMod Docs</a>
 */
@UtilityClass
public class LabyModProtocol {

    public static final String CHANNEL = "labymod3:main";

    /**
     * Send a message to LabyMod
     *
     * @param player         Minecraft Client
     * @param key            LabyMod message key
     * @param messageContent json object content
     */
    public static void sendLabyModMessage(Player player, String key, JsonElement messageContent) {
        byte[] bytes = getBytesToSend(key, messageContent.toString());

        PacketDataSerializer pds = new PacketDataSerializer(Unpooled.wrappedBuffer(bytes));
        PacketPlayOutCustomPayload payloadPacket = new PacketPlayOutCustomPayload(CHANNEL, pds);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(payloadPacket);
    }

    /**
     * Gets the bytes that are required to send the given message
     *
     * @param messageKey      the message's key
     * @param messageContents the message's contents
     * @return the byte array that should be the payload
     */
    public static byte[] getBytesToSend(String messageKey, String messageContents) {
        ByteBuf byteBuf = Unpooled.buffer();

        writeString(byteBuf, messageKey);
        writeString(byteBuf, messageContents);

        // read the buffer
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        byteBuf.release();

        return bytes;
    }

    /**
     * Writes a varint to the given byte buffer
     *
     * @param buf   the byte buffer the int should be written to
     * @param input the int that should be written to the buffer
     */
    private static void writeVarIntToBuffer(ByteBuf buf, int input) {
        while ((input & -128) != 0) {
            buf.writeByte(input & 127 | 128);
            input >>>= 7;
        }

        buf.writeByte(input);
    }

    /**
     * Writes a string to the given byte buffer
     *
     * @param buf    the byte buffer the string should be written to
     * @param string the string that should be written to the buffer
     */
    private static void writeString(ByteBuf buf, String string) {
        byte[] stringBytes = string.getBytes(Charset.forName("UTF-8"));

        if (stringBytes.length > Short.MAX_VALUE) {
            throw new EncoderException("String too big (was " + string.length() + " bytes encoded, max " + Short.MAX_VALUE + ")");
        } else {
            writeVarIntToBuffer(buf, stringBytes.length);
            buf.writeBytes(stringBytes);
        }
    }

}
