package dev.marfien.rewibw.perk;

import org.bukkit.event.Listener;

public class NoOpPerk<T> extends AbstractPerk<T> {

    public NoOpPerk(T defaultPerk) {
        super(defaultPerk, NoOpPerkListener.class);
    }

    public class NoOpPerkListener implements Listener { }

}
