package dev.marfien.rewibw.setuptool;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import java.util.Collection;
import java.util.HashSet;

@RequiredArgsConstructor
public class SetupSession {

    private final World world;
    private final String displayName;
    private final String displayItem;

    private final Collection<Location> bronzeSpawns = new HashSet<>();
    private final Collection<Location> silverSpawns = new HashSet<>();
    private final Collection<Location> goldSpawns = new HashSet<>();
    private final Collection<Location> shops = new HashSet<>();

    private Location spawn;

    public static class TeamInfo {

        private Location spawn;
        private Location bed;
        private BlockFace direction;

    }

}
