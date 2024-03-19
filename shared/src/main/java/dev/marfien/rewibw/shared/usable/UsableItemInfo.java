package dev.marfien.rewibw.shared.usable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.function.Consumer;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UsableItemInfo {

    private final ConsumeType consumeType;
    private Consumer<PlayerInteractEvent> handler = null;

    protected boolean onClick(PlayerInteractEvent event) {
        this.handler.accept(event);
        return true;
    }

}
