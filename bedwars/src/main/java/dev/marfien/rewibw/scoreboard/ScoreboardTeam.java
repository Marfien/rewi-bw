package dev.marfien.rewibw.scoreboard;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Getter
public class ScoreboardTeam {
    
    private final Set<String> entries = new HashSet<>();

    private final String name;
    private boolean registered = true;
    
    private String displayName;

    private Function<Player, String> prefixFactory = ignored -> "";
    private Function<Player, String> suffixFactory = ignored -> "";

    @Accessors(prefix = "")
    private boolean canSeeFriendlyInvisibles = false;
    @Accessors(prefix = "")
    private boolean allowFriendlyFire = true;
    private NameTagVisibility nameTagVisibility = NameTagVisibility.ALWAYS;

    ScoreboardTeam(String name) {
        this.name = name;
        this.displayName = name;
    }

    public void setDisplayName(String displayName) {
        Validate.notNull(displayName, "Display name cannot be null");
        Validate.isTrue(displayName.length() <= 32, "Display name '" + displayName + "' is longer than the limit of 32 characters");
        this.displayName = displayName;
        this.tryUpdate(((player, team) -> team.setDisplayName(displayName)));
    }

    public String getPrefix(Player player) {
        return this.prefixFactory.apply(player);
    }

    public void setPrefix(Function<Player, String> producer) {
        this.prefixFactory = Objects.requireNonNull(producer, "Producer cannot be null");
        this.tryUpdate(((player, team) -> team.setPrefix(this.prefixFactory.apply(player))));
    }

    public void updatePrefix() {
        this.tryUpdate(((player, team) -> team.setPrefix(this.prefixFactory.apply(player))));
    }

    public String getSuffix(Player player) {
        return this.suffixFactory.apply(player);
    }

    public void setSuffix(Function<Player, String> producer) {
        this.suffixFactory = Objects.requireNonNull(producer, "Producer cannot be null");
        this.tryUpdate(((player, team) -> team.setSuffix(this.suffixFactory.apply(player))));
    }

    public void updateSuffix() {
        this.tryUpdate(((player, team) -> team.setSuffix(this.suffixFactory.apply(player))));
    }

    public void setAllowFriendlyFire(boolean allowFriendlyFire) {
        if (allowFriendlyFire == this.allowFriendlyFire) return;

        this.allowFriendlyFire = allowFriendlyFire;
        this.tryUpdate(((player, team) -> team.setAllowFriendlyFire(allowFriendlyFire)));
    }

    public void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
        if (canSeeFriendlyInvisibles == this.canSeeFriendlyInvisibles) return;

        this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
        this.tryUpdate((player, team) -> team.setCanSeeFriendlyInvisibles(canSeeFriendlyInvisibles));
    }

    public void setNameTagVisibility(NameTagVisibility nameTagVisibility) {
        if (nameTagVisibility == this.nameTagVisibility) return;

        this.nameTagVisibility = Objects.requireNonNull(nameTagVisibility, "NameTagVisibility is null");
        this.tryUpdate((player, team) -> team.setNameTagVisibility(nameTagVisibility));
    }

    public Set<String> getEntries() {
        return Collections.unmodifiableSet(this.entries);
    }

    public int getSize() {
        return this.entries.size();
    }

    public void addEntry(String entry) {
        Validate.notNull(displayName, "Entry cannot be null");
        Validate.isTrue(displayName.length() <= 40, "Entry '" + displayName + "' is longer than the limit of 40 characters");

        if (this.entries.add(entry)) {
            tryUpdate(((player, team) -> team.addEntry(entry)));
        }
    }

    public boolean removeEntry(String s) {
        boolean changed = this.entries.remove(s);
        tryUpdate(((player, team) -> team.removeEntry(s)));
        return changed;
    }

    public void unregister() {
        CustomScoreboardManager.unregisterTeam(this.name);
    }

    public boolean hasEntry(String s) {
        return false;
    }

    public void apply(Player player, Team team) {
        team.setDisplayName(this.displayName);
        team.setSuffix(this.suffixFactory.apply(player));
        team.setPrefix(this.prefixFactory.apply(player));
        team.setAllowFriendlyFire(this.allowFriendlyFire);
        team.setCanSeeFriendlyInvisibles(this.canSeeFriendlyInvisibles);
        team.setNameTagVisibility(this.nameTagVisibility);

        for (String entry : this.entries) {
            team.addEntry(entry);
        }
    }

    public void unregistered() {
        this.registered = false;
    }
    
    private void tryUpdate(BiConsumer<Player, Team> updater) {
        if (this.registered) {
            CustomScoreboardManager.updateTeam(this.name, updater);
        }
    }
    
}
