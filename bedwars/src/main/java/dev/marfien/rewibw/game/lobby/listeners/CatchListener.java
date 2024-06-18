package dev.marfien.rewibw.game.lobby.listeners;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.GameStateManager;
import dev.marfien.rewibw.game.lobby.LobbyGameState;
import dev.marfien.rewibw.shared.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public class CatchListener implements Listener {

    private static final int MIN_PLAYERS = 1;

    private static final ItemBuilder RAINBOW_HEAD = ItemBuilder.of(Material.WOOL)
            .setDisplayName("§c§lF§aa§6n§2g §em§2i§9c§dh§b!");

    private Player catchee;

    public CatchListener() {
        new BukkitRunnable() {

            private int counter;

            @Override
            public void run() {
                if (!(GameStateManager.getActiveGameState() instanceof LobbyGameState)) {
                    this.cancel();
                    return;
                }

                if (catchee == null) return;

                catchee.getInventory().setHelmet(nextColor());

                if (this.counter++ % 30 == 0) {
                    Firework firework = catchee.getWorld().spawn(catchee.getLocation(), Firework.class);
                    FireworkMeta meta = firework.getFireworkMeta();
                    meta.addEffect(createRandomEffect());
                    meta.setPower(10);
                    firework.setFireworkMeta(meta);
                }
            }
        }.runTaskTimer(RewiBWPlugin.getInstance(), 0L, 20L);
    }

    private static FireworkEffect createRandomEffect() {
        FireworkEffect.Type[] types = FireworkEffect.Type.values();
        FireworkEffect.Builder builder = FireworkEffect.builder()
                .withColor(Color.fromRGB(ThreadLocalRandom.current().nextInt(0xFFFFFF)))
                .with(types[ThreadLocalRandom.current().nextInt(types.length)])
                .withTrail();

        if (ThreadLocalRandom.current().nextBoolean()) {
            builder.withFlicker();
        }

        return builder.build();
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if (players.size() < MIN_PLAYERS && this.catchee != null) {
            this.catchee.getInventory().setHelmet(null);
            this.catchee = null;
            return;
        }

        if (this.catchee == event.getPlayer()) {
            this.findNewCachee();
        }
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if (this.catchee == null && players.size() >= MIN_PLAYERS) {
            this.findNewCachee();
        }
    }

    @EventHandler
    private void onPlayerCatch(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (!(damager instanceof Player) || !(entity instanceof Player)) {
            return;
        }

        Player catcher = (Player) damager;
        Player catchee = (Player) entity;

        if (this.catchee == catchee) {
            this.setCatchee(catcher);
        }
    }

    private void setCatchee(Player player) {
        if (this.catchee != null) {
            this.catchee.getInventory().setHelmet(null);
            this.catchee.sendMessage("§8[§3Catch§8] " + Message.CATCH_CATCHED.format(player.getDisplayName()));
        }

        player.sendMessage("§8[§3Catch§8] " + Message.CATCH_SELECTED);
        this.catchee = player;
        this.catchee.getInventory().setHelmet(nextColor());
    }

    private static ItemStack nextColor() {
        return RAINBOW_HEAD.setDamage((short) ThreadLocalRandom.current().nextInt(16))
                .asItemStack();
    }

    private void findNewCachee() {
        this.setCatchee(
                Bukkit.getOnlinePlayers()
                        .stream()
                        .filter(player -> player != this.catchee)
                        .findAny()
                        .orElse(null)
        );
    }

}
