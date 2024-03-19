package dev.marfien.rewibw.fakeentities;

import net.minecraft.server.v1_8_R3.DataWatcher;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class FakeHologram extends FakeEntityLiving {

    private final Function<Player, String> textFunction;

    public FakeHologram(Location location, Function<Player, String> textFunction, String initialText) {
        super(new MobEquipment(), location, EntityType.ARMOR_STAND, 2, false);
        this.textFunction = textFunction;
        this.setCustomName(initialText);
        DataWatcher watcher = super.getDataWatcher();
        watcher.a(10, (byte) 0);
        watcher.a(0, (byte) 0x20);
    }

    @Override
    public void updateMetadata() {
        for (Player player : super.getLoadedPlayers()) {
            DataWatcher watcher = new DataWatcher(null);
            watcher.a(10, (byte) (0x10 | 0x08));
            watcher.a(0, (byte) 0x20);
            watcher.a(3, (byte) 1);
            watcher.a(2, this.textFunction.apply(player));
            super.sendMetadataPacket(player, watcher);
        }
    }
}
