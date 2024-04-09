package dev.marfien.rewibw.fakeentities;

import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class FakeEntityLiving extends AbstractFakeEntity {

    private static final Field[] SPAWN_PACKET_FIELDS = new Field[12];

    private static final int
            ENTITY_ID = 0,
            ENTITY_TYPE_ID = 1,
            LOCATION_X = 2,
            LOCATION_Y = 3,
            LOCATION_Z = 4,
            MOTION_X = 5,
            MOTION_Y = 6,
            MOTION_Z = 7,
            LOCATION_YAW = 8,
            LOCATION_PITCH = 9,
            HEAD_YAW = 10,
            DATA_WATCHER = 11;

    static {
        try {

            Class<PacketPlayOutSpawnEntityLiving> spawnPacketClass = PacketPlayOutSpawnEntityLiving.class;
            SPAWN_PACKET_FIELDS[ENTITY_ID] = spawnPacketClass.getDeclaredField("a");
            SPAWN_PACKET_FIELDS[ENTITY_TYPE_ID] = spawnPacketClass.getDeclaredField("b");
            SPAWN_PACKET_FIELDS[LOCATION_X] = spawnPacketClass.getDeclaredField("c");
            SPAWN_PACKET_FIELDS[LOCATION_Y] = spawnPacketClass.getDeclaredField("d");
            SPAWN_PACKET_FIELDS[LOCATION_Z] = spawnPacketClass.getDeclaredField("e");
            SPAWN_PACKET_FIELDS[MOTION_X] = spawnPacketClass.getDeclaredField("f");
            SPAWN_PACKET_FIELDS[MOTION_Y] = spawnPacketClass.getDeclaredField("g");
            SPAWN_PACKET_FIELDS[MOTION_Z] = spawnPacketClass.getDeclaredField("h");
            SPAWN_PACKET_FIELDS[LOCATION_YAW] = spawnPacketClass.getDeclaredField("i");
            SPAWN_PACKET_FIELDS[LOCATION_PITCH] = spawnPacketClass.getDeclaredField("j");
            SPAWN_PACKET_FIELDS[HEAD_YAW] = spawnPacketClass.getDeclaredField("k");
            SPAWN_PACKET_FIELDS[DATA_WATCHER] = spawnPacketClass.getDeclaredField("l");
        } catch (NoSuchFieldException e) {
            // ignored
        }

        for (Field spawnPacketField : FakeEntityLiving.SPAWN_PACKET_FIELDS) {
            spawnPacketField.setAccessible(true);
        }
    }

    private final DataWatcher watcher = new DataWatcher(null);

    public FakeEntityLiving(MobEquipment equipment, Location location, EntityType entityType, int height, boolean lookAtPlayer) {
        super(equipment, location, entityType, height, lookAtPlayer);
    }

    public void setCustomName(String name) {
        name = name.isEmpty() ? null : cutName(name);
        if (name == null) {
            this.watcher.a(3, (byte) 0);
            this.watcher.a(2, "");
        } else {
            this.watcher.a(3, (byte) 1);
            this.watcher.a(2, name);
        }

        this.updateMetadata();
    }

    private String cutName(String name) {
        return name.length() > 32 ? name.substring(0, 32) : name;
    }

    public void updateMetadata() {
        for (Player player : super.getLoadedPlayers()) {
            this.sendMetadataPacket(player, this.getDataWatcher());
        }
    }

    protected void sendMetadataPacket(Player player, DataWatcher watcher) {
        sendPacket(player, new PacketPlayOutEntityMetadata(super.getEntityId(), watcher, true));
    }

    @Override
    protected void spawn(Player player) {
        try {
            sendSpawnPacket(player);
            sendMetadataPacket(player, this.getDataWatcher());
        } catch (IllegalAccessException e) {
            // irrelevant
        }
    }

    private void sendSpawnPacket(Player player) throws IllegalAccessException {
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving();
        Location location = super.getLocation();

        SPAWN_PACKET_FIELDS[ENTITY_ID].set(packet, super.getEntityId());
        SPAWN_PACKET_FIELDS[ENTITY_TYPE_ID].set(packet, super.getEntityType().getTypeId());
        SPAWN_PACKET_FIELDS[LOCATION_X].set(packet, FakeEntity.getFixedLocation(location.getX()));
        SPAWN_PACKET_FIELDS[LOCATION_Y].set(packet, FakeEntity.getFixedLocation(location.getY()));
        SPAWN_PACKET_FIELDS[LOCATION_Z].set(packet, FakeEntity.getFixedLocation(location.getZ()));
        SPAWN_PACKET_FIELDS[LOCATION_YAW].set(packet, FakeEntity.getFixedRotation(location.getYaw()));
        SPAWN_PACKET_FIELDS[HEAD_YAW].set(packet, FakeEntity.getFixedRotation(location.getYaw()));
        SPAWN_PACKET_FIELDS[LOCATION_PITCH].set(packet, FakeEntity.getFixedRotation(location.getPitch()));
        SPAWN_PACKET_FIELDS[DATA_WATCHER].set(packet, getDataWatcher());

        sendPacket(player, packet);
    }

    protected DataWatcher getDataWatcher() {
        return watcher;
    }

}
