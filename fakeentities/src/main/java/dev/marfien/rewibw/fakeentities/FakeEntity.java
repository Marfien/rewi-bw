package dev.marfien.rewibw.fakeentities;

import net.minecraft.server.v1_8_R3.MathHelper;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public interface FakeEntity {

    int getEntityId();

    Location getLocation();

    Chunk getChunk();

    World getWorld();

    EntityType getEntityType();

    boolean isInRange(Location location);

    void loadFor(Player player);

    void unloadFor(Player player);

    MobEquipment getEquipment();

    int getHeight();

    void updateFor(Player player);

    boolean isLookAtPlayer();

    void setLookAtPlayer(boolean lookAtPlayer);

    void onInteract(Player player);

    void onAttack(Player player);

    static int getFixedLocation(double loc) {
        return MathHelper.floor(loc * 32.0D);
    }

    static byte getFixedRotation(float rot) {
        return (byte) ((int) (rot * 256.0F / 360.0F));
    }

    void smoothUpdateFor(Player player);
}
