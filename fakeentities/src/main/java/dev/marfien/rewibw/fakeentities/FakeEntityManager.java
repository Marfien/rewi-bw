package dev.marfien.rewibw.fakeentities;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@UtilityClass
public class FakeEntityManager {

    private static int entityCounter = Integer.MAX_VALUE;

    public static final int VIEW_DISTANCE = 60;
    public static final int VIEW_DISTANCE_SQUARED = VIEW_DISTANCE * VIEW_DISTANCE;

    private static final Map<Integer, FakeEntity> fakeEntities = new HashMap<>();

    @Getter
    private static Plugin plugin;

    public static void init(Plugin plugin) {
        FakeEntityManager.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(new FakeEntityUpdateListener(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new ProtocolListener(), plugin);
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
        return entityCounter--;
    }

    public static void unloadFor(Player player) {
        for (FakeEntity mob : fakeEntities.values()) {
            mob.unloadFor(player);
        }
    }

}
