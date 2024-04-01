package dev.marfien.rewibw.shared;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Getter
@Setter
@NoArgsConstructor
@ConfigSerializable
public class Position extends Vector {

    private float yaw;
    private float pitch;

    public Position(int x, int y, int z) {
        super(x, y, z);
    }

    public Position(double x, double y, double z) {
        super(x, y, z);
    }

    public Position(float x, float y, float z) {
        super(x, y, z);
    }

    public Position(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Position(int x, int y, int z, float yaw, float pitch) {
        super(x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Position(double x, double y, double z, float yaw, float pitch) {
        super(x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Position(float x, float y, float z, float yaw, float pitch) {
        super(x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Chunk getChunk(World world) {
        return world.getChunkAt(super.getBlockX(), super.getBlockZ());
    }

    public Block getBlock(World world) {
        return world.getBlockAt(super.getBlockX(), super.getBlockY(), super.getBlockZ());
    }

    @Override
    public Position setX(double x) {
        super.setX(x);
        return this;
    }

    @Override
    public Position setY(double y) {
        super.setY(y);
        return this;
    }

    @Override
    public Position setZ(double z) {
        super.setZ(z);
        return this;
    }

    public Vector getDirection() {
        Vector vector = new Vector();
        double rotX = this.yaw;
        double rotY = this.pitch;

        vector.setY(-Math.sin(Math.toRadians(rotY)));

        double xz = Math.cos(Math.toRadians(rotY));
        vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
        vector.setZ(xz * Math.cos(Math.toRadians(rotX)));

        return vector;
    }

    public Position setDirection(Vector vector) {
        double x = vector.getX();
        double z = vector.getZ();
        if (x == 0.0 && z == 0.0) {
            this.pitch = (float) (vector.getY() > 0.0 ? -90 : 90);
            return this;
        }

        double theta = Math.atan2(-x, z);
        this.yaw = (float) Math.toDegrees((theta + 2 * Math.PI) % (2 * Math.PI));
        double x2 = NumberConversions.square(x);
        double z2 = NumberConversions.square(z);
        double xz = Math.sqrt(x2 + z2);
        this.pitch = (float) Math.toDegrees(Math.atan(-vector.getY() / xz));
        return this;
    }

    public Position subtract(double x, double y, double z) {
        super.x -= x;
        super.y -= y;
        super.z -= z;
        return this;
    }

    public Position add(double x, double y, double z) {
        super.x += x;
        super.y += y;
        super.z += z;
        return this;
    }

    public Vector toVector() {
        return this;
    }

    public Location toLocation(World world) {
        return new Location(world, super.x, super.y, super.z, this.yaw, this.pitch);
    }

    @Override
    public Position clone() {
        return new Position();
    }
}
