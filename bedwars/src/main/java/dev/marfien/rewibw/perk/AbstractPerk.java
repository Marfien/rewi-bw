package dev.marfien.rewibw.perk;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPerk<T> implements Perk<T> {

    private final Map<Player, T> perks = new HashMap<>();

    private final T defaultPerk;
    @Getter
    private final Listener listener;

    @SneakyThrows({NoSuchMethodException.class, IllegalAccessException.class, InvocationTargetException.class, InstantiationException.class})
    protected AbstractPerk(T defaultPerk, Class<? extends Listener> listenerClass) {
        this.defaultPerk = defaultPerk;
        this.listener = listenerClass.getConstructor(this.getClass()).newInstance(this);
    }

    @Override
    public T getPerk(Player player) {
        return this.perks.get(player);
    }

    @Override
    public T getDefault() {
        return this.defaultPerk;
    }

    @Override
    public void setPerk(Player player, T perk) {
        this.perks.put(player, perk);
    }

    @Override
    public void unsetPerk(Player player) {
        this.perks.remove(player);
    }
}
