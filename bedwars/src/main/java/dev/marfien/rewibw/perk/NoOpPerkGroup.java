package dev.marfien.rewibw.perk;

import org.bukkit.inventory.ItemStack;

public class NoOpPerkGroup<P extends Perk> extends PerkGroup<P> {

    @SafeVarargs
    public NoOpPerkGroup(ItemStack displayItem, P defaultPerk, P... perks) {
        super(displayItem, defaultPerk, perks);
    }

}
