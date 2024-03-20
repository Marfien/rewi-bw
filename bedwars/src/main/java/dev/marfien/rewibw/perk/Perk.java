package dev.marfien.rewibw.perk;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface Perk<T> {

    T getPerk(Player player);

    T getDefault();

    void setPerk(Player player, T perk);

    void unsetPerk(Player player);

    Listener getListener();

    default T getOrDefault(Player player) {
        T perk = getPerk(player);
        return perk == null ? getDefault() : perk;
    }

}
