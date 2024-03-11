package dev.marfien.rewibw.fakemobs;

import dev.marfien.rewibw.RewiBWPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeEntityManager {

    private static int ENTITY_COUNTER = Integer.MAX_VALUE;

    public static final int VIEW_DISTANCE = 60;
    public static final double VIEW_DISTANCE_SQUARED = VIEW_DISTANCE * VIEW_DISTANCE;

    private static final Map<Integer, FakeEntity> fakeEntities = new HashMap<>();

    public static void init() {
        Bukkit.getPluginManager().registerEvents(new FakeEntityUpdateListener(), RewiBWPlugin.getInstance());
        Bukkit.getPluginManager().registerEvents(new ProtocolListener(), RewiBWPlugin.getInstance());
    }

    public static Collection<FakeEntity> getFakeEntities() {
        return fakeEntities.values();
    }

    public static Optional<FakeEntity> getFakeEntity(int entityId) {
        return Optional.ofNullable(fakeEntities.get(entityId));
    }

    public static void updateView(Player player) {
        for (FakeEntity mob : fakeEntities.values()) {
            mob.updateFor(player);
        }
    }

    public static void smoothUpdateView(Player player) {
        for (FakeEntity mob : fakeEntities.values()) {
            mob.smoothUpdateFor(player);
        }
    }

    public static void spawn(FakeEntity mob) {
        fakeEntities.put(mob.getEntityId(), mob);
        for (Player player : mob.getWorld().getPlayers()) {
            mob.updateFor(player);
        }
    }

    public static void destroy(FakeEntity mob) {
        if (!fakeEntities.remove(mob.getEntityId(), mob)) return;

        for (Player player : mob.getWorld().getPlayers()) {
            mob.unloadFor(player);
        }
    }

    public static void destroy(int mobId) {
        FakeEntity mob = fakeEntities.remove(mobId);

        if (mob == null) return;

        for (Player player : mob.getWorld().getPlayers()) {
            mob.unloadFor(player);
        }
    }

    public static int nextEntityId() {
        return ENTITY_COUNTER--;
    }
}
