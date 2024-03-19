package dev.marfien.rewibw.setuptool;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;

@Getter
@Setter
@RequiredArgsConstructor
public class SetupSession {

    private final World world;
    private final String displayName;
    private final String displayItem;

    private final Collection<Location> bronzeSpawns = new HashSet<>();
    private final Collection<Location> silverSpawns = new HashSet<>();
    private final Collection<Location> goldSpawns = new HashSet<>();
    private final Collection<Location> shops = new HashSet<>();

    private int borderX1;
    private int borderX2;
    private int borderZ1;
    private int borderZ2;

    private Location spawn;

    public void save(Path path) {

    }

    public static class TeamInfo {

        private Location spawn;
        private Location bed;
        private BlockFace direction;

    }

}
