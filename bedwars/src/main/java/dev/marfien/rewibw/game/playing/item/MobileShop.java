package dev.marfien.rewibw.game.playing.item;

import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.fakemobs.FakeEntityManager;
import dev.marfien.rewibw.shop.FakeDealer;
import dev.marfien.rewibw.usable.ConsumeType;
import dev.marfien.rewibw.usable.UsableItemInfo;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class MobileShop extends UsableItemInfo {

    private final int SECONDS_TO_DESPAWN = 20;

    public MobileShop() {
        super(ConsumeType.DECREASE_AMOUNT);
    }

    @Override
    protected boolean onClick(PlayerInteractEvent event) {
        Player clicker = event.getPlayer();
        if (!event.hasBlock()) return false;

        if (event.getBlockFace() != BlockFace.UP) {
            clicker.sendMessage(RewiBWPlugin.PREFIX + "§cDu kannst du Mobilen Shop nur auf dem Boden platzieren.");
            return false;
        }

        Location location = event.getClickedBlock().getLocation().add(0.5, 1.0, 0.5);
        location.setDirection(clicker.getLocation().toVector().subtract(location.toVector()));
        FakeDealer dealer = new FakeDealer(location);
        FakeEntityManager.spawn(dealer);
        ArmorStand timeDisplay = location.getWorld().spawn(location.add(0, 2.2, 0), ArmorStand.class);
        timeDisplay.setMarker(true);
        timeDisplay.setGravity(false);
        timeDisplay.setVisible(false);
        timeDisplay.setCustomNameVisible(true);
        timeDisplay.setCustomName("§320 Sekunden");
        new BukkitRunnable() {

            private int remainingSeconds = SECONDS_TO_DESPAWN;

            @Override
            public void run() {
                if (this.remainingSeconds <= 0) {
                    FakeEntityManager.destroy(dealer);
                    timeDisplay.remove();
                    Location dealerLoc = dealer.getLocation();
                    World world = dealerLoc.getWorld();
                    world.playEffect(dealerLoc, Effect.SMOKE, 0);
                    world.playSound(dealerLoc, Sound.ENDERMAN_TELEPORT, 1F, 1F);
                    this.cancel();
                    return;
                }

                timeDisplay.setCustomName("§3" + remainingSeconds + " Sekunden");
                this.remainingSeconds--;
            }
        }.runTaskTimer(RewiBWPlugin.getInstance(), 0, 20);
        return true;
    }
}
