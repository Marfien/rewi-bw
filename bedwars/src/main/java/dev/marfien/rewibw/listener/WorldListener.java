package dev.marfien.rewibw.listener;

import dev.marfien.rewibw.RewiBWPlugin;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.logging.Level;

public class WorldListener implements Listener {

    @EventHandler
    private void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onLeafDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onWorldInit(WorldInitEvent event) {
        event.getWorld().setKeepSpawnInMemory(false);
    }

    @EventHandler
    private void onWorldCreate(WorldLoadEvent event) {
        World world = event.getWorld();
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setAutoSave(false);
        world.setDifficulty(Difficulty.NORMAL);
    }

    @EventHandler
    private void onEntitySpawn(CreatureSpawnEvent event) {
        switch (event.getSpawnReason()) {
            case NATURAL:
            case JOCKEY:
            case CHUNK_GEN:
            case VILLAGE_INVASION:
            case DEFAULT:
            case REINFORCEMENTS:
            case VILLAGE_DEFENSE:
            case SILVERFISH_BLOCK:
                event.setCancelled(true);
        }
    }

    @EventHandler
    private void onUnload(WorldUnloadEvent event) {
        RewiBWPlugin.getPluginLogger().log(Level.INFO, "Unloading world {}", event.getWorld().getName());
    }

    @EventHandler
    private void onBlockPhysics(BlockPhysicsEvent event) {
        if (event.getBlock().getType() == Material.DOUBLE_PLANT) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onHanginBreak(HangingBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onHangingPlace(HangingPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onCraft(CraftItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onGrow(BlockGrowEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onFire(BlockIgniteEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onBedInteract(PlayerBedEnterEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onAchievement(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onFade(BlockFadeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onFromTo(BlockFromToEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

}
