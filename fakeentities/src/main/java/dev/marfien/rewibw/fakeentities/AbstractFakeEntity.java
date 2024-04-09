package dev.marfien.rewibw.fakeentities;

import io.netty.buffer.Unpooled;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

@Getter
public abstract class AbstractFakeEntity implements FakeEntity {

    @Getter(AccessLevel.PROTECTED)
    private final Collection<Player> loadedPlayers = new HashSet<>();

    private final Collection<Consumer<Player>> onAttackListeners = new ArrayList<>();
    private final Collection<Consumer<Player>> onInteractListeners = new ArrayList<>();

    private final int entityId = FakeEntityManager.nextEntityId();

    private final MobEquipment equipment;
    private final EntityType entityType;
    private final int height;

    private Location location;
    private boolean lookAtPlayer;

    protected AbstractFakeEntity(MobEquipment equipment, Location location, EntityType entityType, int height, boolean lookAtPlayer) {
        this.equipment = equipment;
        this.location = location.clone();
        this.entityType = entityType;
        this.height = height;
        this.lookAtPlayer = lookAtPlayer;
    }

    public void addAttackListener(Consumer<Player> listener) {
        this.onAttackListeners.add(listener);
    }

    public void addInteractListener(Consumer<Player> listener) {
        this.onInteractListeners.add(listener);
    }

    @Override
    public void onAttack(Player player) {
        this.onAttackListeners.forEach(listener -> listener.accept(player));
    }

    @Override
    public void onInteract(Player player) {
        this.onInteractListeners.forEach(listener -> listener.accept(player));
    }

    public Location getLocation() {
        return this.location.clone();
    }

    @Override
    public Chunk getChunk() {
        return this.location.getChunk();
    }

    @Override
    public World getWorld() {
        return this.location.getWorld();
    }

    @Override
    public boolean isInRange(Location location) {
        return location.getWorld() == this.getWorld()
                && this.location.distanceSquared(location) <= FakeEntityManager.VIEW_DISTANCE_SQUARED;
    }

    public boolean isPlayerLoaded(Player player) {
        return this.loadedPlayers.contains(player);
    }

    public void setLookAtPlayer(boolean lookAtPlayer) {
        if (this.lookAtPlayer == lookAtPlayer) return;

        this.lookAtPlayer = lookAtPlayer;
        for (Player player : this.loadedPlayers) {
            if (lookAtPlayer) {
                this.updateLookAtPlayer(player);
            }
        }
    }

    private void updateLookAtPlayer(Player player) {
        if (!this.lookAtPlayer) return;
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        double dX = entityPlayer.locX - this.location.getX();
        double dY = entityPlayer.locY - this.location.getY();
        double dZ = entityPlayer.locZ - this.location.getZ();

        if (player.getWorld() != this.getWorld()
                || NumberConversions.square(dX) + NumberConversions.square(dY) + NumberConversions.square(dZ) > 25) {
            return;
        }

        // TODO: Implement Entity Eye height
        double dXZ = Math.sqrt(dX * dX + dZ * dZ);
        double newYaw = Math.acos(dX / dXZ) * 180.0 / Math.PI;
        if (dZ < 0.0) {
            newYaw += Math.abs(180.0 - newYaw) * 2.0;
        }

        float yaw = (float) (newYaw - 98.0);
        float pitch = (float) (-Math.atan((dY) / dXZ) * 180.0 / Math.PI);

        this.sendPositionPacket(player, this.location.getX(), this.location.getY(), this.location.getZ(), yaw, pitch);
        this.sendHeadRotation(player, yaw);
    }

    @SneakyThrows
    public void playAnimation(Player player, byte animation) {
        PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
        PacketDataSerializer serializer = new PacketDataSerializer(Unpooled.buffer());
        serializer.b(this.getEntityId());
        serializer.writeByte(animation);
        packet.a(serializer);
        sendPacket(player, packet);
    }

    public void playDamageAnimation(Player player) {
        this.playAnimation(player, (byte) 1);
    }

    @Override
    public void smoothUpdateFor(Player player) {
        this.updateLookAtPlayer(player);
    }

    @Override
    public void updateFor(Player player) {
        Location playerLocation = player.getLocation();
        if (!this.isInRange(playerLocation)) {
            this.unloadFor(player);
            return;
        }

        this.loadFor(player);
    }

    @Override
    public void loadFor(Player player) {
        if (!this.loadedPlayers.add(player)) return;

        this.spawn(player);
        this.sendHeadRotation(player, this.location.getYaw());
        this.sendEquipmentPackets(player);
        this.sendPositionPacket(player, this.location.getX(), this.location.getY(), this.location.getZ(), this.location.getYaw(), this.location.getPitch());
    }

    @Override
    public void unloadFor(Player player) {
        if (!this.loadedPlayers.remove(player)) return;

        this.despawn(player);
    }

    protected abstract void spawn(Player player);

    private void despawn(Player player) {
        sendPacket(player, new PacketPlayOutEntityDestroy(this.entityId));
    }

    protected static void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @SneakyThrows
    protected void sendHeadRotation(Player player, float yaw) {
        PacketPlayOutEntityHeadRotation packet = new PacketPlayOutEntityHeadRotation();
        PacketDataSerializer serializer = new PacketDataSerializer(Unpooled.buffer());
        serializer.b(this.entityId);
        serializer.writeByte(FakeEntity.getFixedRotation(yaw));
        packet.a(serializer);
        sendPacket(player, packet);
    }

    private void sendPositionPacket(Player player, double x, double y, double z, float yaw, float pitch) {
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(
                this.entityId,
                FakeEntity.getFixedLocation(x),
                FakeEntity.getFixedLocation(y),
                FakeEntity.getFixedLocation(z),
                FakeEntity.getFixedRotation(yaw),
                FakeEntity.getFixedRotation(pitch),
                false
        );

        sendPacket(player, packet);
    }

    public void teleport(Location location) {
        if (location == null)
            throw new NullPointerException("Location cannot be null");

        this.location = location;
        for (Player player : this.loadedPlayers) {
            this.sendPositionPacket(player, this.location.getX(), this.location.getY(), this.location.getZ(), this.location.getYaw(), this.location.getPitch());
        }
    }

    private void sendEquipmentPackets(Player player) {
        List<Packet<?>> packets = this.equipment.createPackets(this.getEntityId());
        if (packets.isEmpty()) return;

        for (Packet<?> packet : packets) {
            sendPacket(player, packet);
        }
    }

}
