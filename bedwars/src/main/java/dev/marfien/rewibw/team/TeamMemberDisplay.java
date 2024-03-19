package dev.marfien.rewibw.team;

import dev.marfien.rewibw.fakemob.FakeEntityManager;
import dev.marfien.rewibw.fakemob.FakePlayer;
import dev.marfien.rewibw.fakemob.MobEquipment;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@Getter
public class TeamMemberDisplay {

    private final Location location;
    private final MobEquipment mobEquipment = new MobEquipment();

    private Player player;
    private FakePlayer display;

    public TeamMemberDisplay(GameTeam team, Location location) {
        this.location = location.clone();
        this.mobEquipment.setBoots(team.getBoots());
        this.mobEquipment.setLeggings(team.getLeggings());
        this.mobEquipment.setChestplate(team.getChestplate());
    }

    public void setPlayer(Player player) {
        if (player == this.player) return;

        if (this.player != null) {
            this.removePlayer();
        }

        this.setPlayerSafe(player);
    }

    public void removePlayer() {
        synchronized (this) {
            if (this.player == null) return;

            FakeEntityManager.destroy(this.display);
            this.display = null;
            this.player = null;
        }
    }

    private void setPlayerSafe(Player player) {
        synchronized (this) {
            this.player = player;
            this.display = new FakePlayer(
                    this.mobEquipment,
                    this.location,
                    false,
                    ((CraftPlayer) player).getHandle().getProfile()
            );

            FakeEntityManager.spawn(this.display);
        }
    }

    public Location getLocation() {
        return this.location.clone();
    }
}
