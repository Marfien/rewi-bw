package dev.marfien.rewibw.team;

import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.playing.PlayingGameState;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;

@Getter
public class TeamBed implements Listener {

    private final Block firstBedBlock;
    private final Block secondBedBlock;
    private final GameTeam team;

    private boolean alive = true;

    public TeamBed(GameTeam team, Location bedLocation, BlockFace bedDirection) {
        this.team = team;
        this.firstBedBlock = bedLocation.getBlock();
        this.secondBedBlock = this.firstBedBlock.getRelative(bedDirection);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onBedBreak(BlockBreakEvent event) {
        if (!this.alive || !this.isValid(event.getBlock())) return;

        Player breaker = event.getPlayer();
        if (this.team.isMember(breaker)) {
            event.setCancelled(true);
            breaker.sendMessage(RewiBWPlugin.PREFIX + "Du kannst dein eigenes Bett nicht zerstören.");
            return;
        }

        event.setCancelled(false);

        this.alive = false;
        this.team.getDisplayScoreboardTeam().updatePrefix();
        PlayingGameState.getSidebarObjective().removeScore(this.team.getColor().getDisplayName());
        this.team.updateScoreboardEntry();
        Bukkit.broadcastMessage(RewiBWPlugin.PREFIX + "Das Bett von Team " + team.getColor().getDisplayName() + "§7 wurde von " + event.getPlayer().getDisplayName() + " §7zerstört.");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.IRONGOLEM_DEATH, 1F, 0.8F);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onPhysics(BlockPhysicsEvent event) {
        if (!this.isValid(event.getBlock())) return;

        event.setCancelled(false);
    }

    private boolean isValid(Block block) {
        return block.equals(this.firstBedBlock) || block.equals(this.secondBedBlock);
    }

}