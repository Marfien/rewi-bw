package dev.marfien.rewibw.perk;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public abstract class PerkGroup<P extends Perk> {

    private final ItemStack displayItem;
    private final P defaultPerk;

    private final P[] perks;
    @Getter(AccessLevel.NONE)
    private final Map<Player, P> selectedPerks = new HashMap<>();

    @SafeVarargs
    protected PerkGroup(ItemStack displayItem, P defaultPerk, P... perks) {
        this.displayItem = displayItem;
        this.defaultPerk = defaultPerk;
        this.perks = perks;
    }

    public void setPerk(Player player, P perk) {
        this.selectedPerks.put(player, perk);
    }

    public void unsetPerk(Player player) {
        this.selectedPerks.remove(player);
    }

    public Optional<P> getPerk(Player player) {
        return Optional.ofNullable(this.selectedPerks.get(player));
    }

    public P getOrDefault(Player player) {
        return getPerk(player).orElse(this.defaultPerk);
    }

    public abstract void init(Plugin plugin);

}
