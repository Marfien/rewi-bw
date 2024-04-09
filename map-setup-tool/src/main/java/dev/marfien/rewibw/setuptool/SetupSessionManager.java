package dev.marfien.rewibw.setuptool;

import lombok.experimental.UtilityClass;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@UtilityClass
public class SetupSessionManager {

    private static final Map<UUID, SetupSession> sessions = new HashMap<>();

    public static Optional<SetupSession> getSession(UUID uuid) {
        return Optional.ofNullable(sessions.get(uuid));
    }

    public static SetupSession setSessions(UUID uuid, World world) {
        SetupSession session = new SetupSession(uuid, world);
        sessions.put(uuid, session);
        return session;
    }

    public static void removeSession(UUID uuid) {
        sessions.remove(uuid);
    }

    public static SetupSession getOrCreateSession(UUID uuid, World world) {
        return sessions.computeIfAbsent(uuid, key -> new SetupSession(uuid, world));
    }

}
