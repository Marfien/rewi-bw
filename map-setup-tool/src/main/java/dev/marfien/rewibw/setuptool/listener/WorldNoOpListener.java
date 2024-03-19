package dev.marfien.rewibw.setuptool.listener;

import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldNoOpListener implements Listener {

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
    private void onGrow(BlockGrowEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onFire(BlockIgniteEvent event) {
        event.setCancelled(true);
    }

}

