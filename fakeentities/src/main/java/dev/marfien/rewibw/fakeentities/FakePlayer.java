package dev.marfien.rewibw.fakeentities;

import com.mojang.authlib.GameProfile;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.WorldSettings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class FakePlayer extends AbstractFakeEntity {

    private static final Field PLAYER_INFO_DATA_FIELD;
    private static final Field[] SPAWN_PACKET_FIELDS = new Field[9];

    private static final int
            ENTITY_ID = 0,
            PROFILE_UUID = 1,
            LOCATION_X = 2,
            LOCATION_Y = 3,
            LOCATION_Z = 4,
            LOCATION_YAW = 5,
            LOCATION_PITCH = 6,
            ITEM_IN_HAND_ID = 7,
            DATAWATCHER = 8;

    static {
        Field field = null;
        try {
            field = PacketPlayOutPlayerInfo.class.getDeclaredField("b");
            field.setAccessible(true);

            Class<PacketPlayOutNamedEntitySpawn> spawnPacketClass = PacketPlayOutNamedEntitySpawn.class;
            SPAWN_PACKET_FIELDS[ENTITY_ID] = spawnPacketClass.getDeclaredField("a");
            SPAWN_PACKET_FIELDS[PROFILE_UUID] = spawnPacketClass.getDeclaredField("b");
            SPAWN_PACKET_FIELDS[LOCATION_X] = spawnPacketClass.getDeclaredField("c");
            SPAWN_PACKET_FIELDS[LOCATION_Y] = spawnPacketClass.getDeclaredField("d");
            SPAWN_PACKET_FIELDS[LOCATION_Z] = spawnPacketClass.getDeclaredField("e");
            SPAWN_PACKET_FIELDS[LOCATION_YAW] = spawnPacketClass.getDeclaredField("f");
            SPAWN_PACKET_FIELDS[LOCATION_PITCH] = spawnPacketClass.getDeclaredField("g");
            SPAWN_PACKET_FIELDS[ITEM_IN_HAND_ID] = spawnPacketClass.getDeclaredField("h");
            SPAWN_PACKET_FIELDS[DATAWATCHER] = spawnPacketClass.getDeclaredField("i");
        } catch (NoSuchFieldException e) {
            System.err.println("Failed to initialize FakePlayer fields");
        }

        PLAYER_INFO_DATA_FIELD = field;
        for (Field spawnPacketField : FakePlayer.SPAWN_PACKET_FIELDS) {
            spawnPacketField.setAccessible(true);
        }
    }

    private final GameProfile profile;

    public FakePlayer(MobEquipment equipment, Location location, boolean lookAtPlayer, GameProfile profile) {
        super(equipment, location, EntityType.PLAYER, 2, lookAtPlayer);

        if (profile == null) {
            this.profile = null;
            return;
        }

        this.profile = new GameProfile(UUID.randomUUID(), profile.getName());
        profile.getProperties().get("textures").forEach(property -> this.profile.getProperties().put("textures", property));
    }

    @Override
    @SneakyThrows
    protected void spawn(Player player) {
        this.addToTabList(player);
        this.sendSpawnPacket(player);
        Bukkit.getScheduler().runTaskLater(FakeEntityManager.getPlugin(), () -> this.removeFromTabList(player), 20 * 2L);
    }

    private void addToTabList(Player player) {
        this.sendTabListInfo(player, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
    }

    private void removeFromTabList(Player player) {
        this.sendTabListInfo(player, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
    }

    private void sendSpawnPacket(Player player) throws IllegalAccessException {
        Location location = super.getLocation();
        PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();

        SPAWN_PACKET_FIELDS[ENTITY_ID].set(packet, super.getEntityId());
        SPAWN_PACKET_FIELDS[PROFILE_UUID].set(packet, this.profile.getId());
        SPAWN_PACKET_FIELDS[LOCATION_X].set(packet, FakeEntity.getFixedLocation(location.getX()));
        SPAWN_PACKET_FIELDS[LOCATION_Y].set(packet, FakeEntity.getFixedLocation(location.getY()));
        SPAWN_PACKET_FIELDS[LOCATION_Z].set(packet, FakeEntity.getFixedLocation(location.getZ()));
        SPAWN_PACKET_FIELDS[LOCATION_YAW].set(packet, FakeEntity.getFixedRotation(location.getYaw()));
        SPAWN_PACKET_FIELDS[LOCATION_PITCH].set(packet, FakeEntity.getFixedRotation(location.getPitch()));
        ItemStack inHand = super.getEquipment().getItemInHand();

        SPAWN_PACKET_FIELDS[ITEM_IN_HAND_ID].set(packet, inHand == null ? 0 : inHand.getTypeId());

        DataWatcher watcher = new DataWatcher(null);
        // Show all skin parts
        watcher.a(10, Byte.MAX_VALUE);
        // Set full life
        watcher.a(6, (float) 20);
        SPAWN_PACKET_FIELDS[DATAWATCHER].set(packet, watcher);

        sendPacket(player, packet);
    }

    @SneakyThrows
    private void sendTabListInfo(Player player, PacketPlayOutPlayerInfo.EnumPlayerInfoAction action) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(action);

        List<PacketPlayOutPlayerInfo.PlayerInfoData> infoData = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) PLAYER_INFO_DATA_FIELD.get(packet);
        infoData.add(this.createPlayerInfoPacketData(packet));

        sendPacket(player, packet);
    }

    protected PacketPlayOutPlayerInfo.PlayerInfoData createPlayerInfoPacketData(PacketPlayOutPlayerInfo packet) {
        return packet.new PlayerInfoData(this.profile, 1, WorldSettings.EnumGamemode.NOT_SET,
                CraftChatMessage.fromString(this.profile.getName())[0]);
    }

}