package dev.marfien.rewibw.team;

import dev.marfien.rewibw.Message;
import dev.marfien.rewibw.RewiBWPlugin;
import dev.marfien.rewibw.game.playing.PlayingGameState;
import dev.marfien.rewibw.perk.PerkManager;
import lombok.Getter;
import org.bukkit.Bukkit;
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

    public TeamBed(GameTeam team, Block block, BlockFace direction) {
        this.team = team;
        this.firstBedBlock = block;
        this.secondBedBlock = this.firstBedBlock.getRelative(direction);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onBedBreak(BlockBreakEvent event) {
        if (!this.alive || !this.isValid(event.getBlock())) return;

        Player breaker = event.getPlayer();
        if (this.team.isMember(breaker)) {
            event.setCancelled(true);
            breaker.sendMessage(RewiBWPlugin.PREFIX + Message.OWN_BED_DESTROY);
            return;
        }

        event.setCancelled(false);

        this.alive = false;
        this.team.getDisplayScoreboardTeam().updatePrefix();
        PlayingGameState.getSidebarObjective().removeScore(this.team.getColor().getDisplayName());
        this.team.updateScoreboardEntry();
        Message.broadcast(RewiBWPlugin.PREFIX + Message.BED_DESTROYED.format(this.team.getColor().getDisplayName(), breaker.getDisplayName()));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), PerkManager.BED_DESTROY_SOUND_PERK_GROUP.getOrDefault(player).getData(), 1F, 0.8F);
        }
        for (Player member : this.team.getMembers()) {
            member.sendTitle("§6Dein Bett", "§6wurde zerstört");
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
