package dev.marfien.rewibw;

import dev.marfien.rewibw.game.playing.PlayingGameState;
import dev.marfien.rewibw.util.Items;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

public class PlayerManager {

    private static final Collection<Player> SPECTATORS = new HashSet<>();

    public static void resetPlayerStatus(Player player) {
        player.setOp(true);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setFireTicks(0);
        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);
        player.setFallDistance(0);
        player.setGameMode(GameMode.SURVIVAL);
        PlayerInventory inventory = player.getInventory();
        inventory.setHeldItemSlot(0);
        inventory.clear();
        inventory.setArmorContents(null);
    }

    public static void setSpectator(Player player) {
        SPECTATORS.add(player);
        resetPlayerStatus(player);
        player.setAllowFlight(true);

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other == player) continue;
            other.hidePlayer(player);
        }

        Bukkit.getScheduler().runTaskLater(RewiBWPlugin.getInstance(), () -> player.setFlying(true), 10);

        Inventory inventory = player.getInventory();
        inventory.setItem(0, Items.SPECTATOR_COMPASS);
        inventory.setItem(8, Items.QUIT_ITEM);
    }

    public static void removeSpectator(Player player) {
        SPECTATORS.remove(player);
        resetPlayerStatus(player);
        player.setAllowFlight(false);
        player.setFlying(false);
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other == player) continue;
            other.showPlayer(player);
        }
    }

    public static boolean isSpectator(Player player) {
        return SPECTATORS.contains(player);
    }

    public static void hideSpectators(Player player) {
        for (Player spectator : SPECTATORS) {
            if (spectator == player) continue;
            player.hidePlayer(spectator);
        }
    }

    public static void forAllSpectators(Consumer<Player> action) {
        SPECTATORS.forEach(action);
    }

    public static void showSpectators(Player player) {
        for (Player spectator : SPECTATORS) {
            if (spectator == player) continue;
            player.showPlayer(spectator);
        }
    }
}
