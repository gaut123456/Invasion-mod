package com.example.invasionmod.mobs;

import com.example.invasionmod.config.Config;
import com.example.invasionmod.utils.MessageUtils;
import com.example.invasionmod.utils.MobUtils;
import com.example.invasionmod.utils.ScalingDataUtils;
import com.example.invasionmod.UIOverlayRenderer.InvasionPhaseUI;
import net.minecraft.server.level.ServerLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Invasion {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Random random = new Random();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean invasionStarted = false;

    // Configuration values
    private final int initialMobsPerSpawn = Config.getInitialMobsPerSpawn();
    private final int scalingMobsPerInterval = Config.getScalingMobsPerInterval();
    private final int scaleInterval = Config.getScaleInterval();  // 30 seconds
    private final int spawnInterval = Config.getSpawnInterval();  // 10 seconds
    private final int maxInvasionDuration = Config.getInvasionDuration();
    private final boolean debugLoggingEnabled = Config.isDebugLoggingEnabled();

    // Timers and mob counters
    private final AtomicInteger invasionDuration = new AtomicInteger(0);
    private final AtomicInteger mobsToSpawn = new AtomicInteger();
    private final AtomicInteger totalSpawnedMobs = new AtomicInteger(0);
    private boolean scalingStarted = false;
    private long lastScaleTime = 0;

    public void startSpawning(ServerLevel world) {
        String worldName = world.getServer().getWorldData().getLevelName();

        // Initialize mobsToSpawn to the initial configuration value
        mobsToSpawn.set(initialMobsPerSpawn);

        // Load the saved scaling data
        mobsToSpawn.set(ScalingDataUtils.loadScalingData(worldName));

        if (debugLoggingEnabled) {
            LOGGER.info("Starting invasion with parameters:");
            LOGGER.info("Initial Mobs per Spawn: {}", initialMobsPerSpawn);
            LOGGER.info("Scaling Mobs per Interval: {}", scalingMobsPerInterval);
            LOGGER.info("Scale Interval: {} seconds", scaleInterval);
            LOGGER.info("Spawn Interval: {} seconds", spawnInterval);
            LOGGER.info("Loaded Mobs to Spawn: {}", mobsToSpawn.get());
        }

        // Start the invasion
        scheduler.scheduleAtFixedRate(() -> {
            int mobsToSpawnNow = mobsToSpawn.get();

            if (!invasionStarted) {
                MessageUtils.sendMessageToAllPlayers(world, "Invasion is starting!");
                invasionStarted = true;
                InvasionPhaseUI.updateInvasionState(mobsToSpawn.get());
            }

            world.getServer().execute(() -> {
                for (int i = 0; i < mobsToSpawnNow; i++) {
                    MobUtils.spawnRandomMob(world, random);
                }

                // Update total spawned mobs
                totalSpawnedMobs.addAndGet(mobsToSpawnNow);

                MessageUtils.sendMessageToAllPlayers(world, mobsToSpawnNow + " mobs spawned!");

                // Update UI with current phase, remaining time, and total spawned mobs
                InvasionPhaseUI.updateInvasionState(mobsToSpawnNow);
            });

            // Start scaling after the first wave
            if (!scalingStarted) {
                scalingStarted = true;
                lastScaleTime = System.currentTimeMillis();  // Set initial time after first wave
            }

            // Check if it's time to scale the mob count (based on real time, not intervals)
            long currentTime = System.currentTimeMillis();
            if (scalingStarted && currentTime - lastScaleTime >= scaleInterval * 1000L) {
                mobsToSpawn.addAndGet(scalingMobsPerInterval);  // Increase mob count every 30 seconds after first wave
                lastScaleTime = currentTime;  // Reset the time of last scaling

                if (debugLoggingEnabled) {
                    LOGGER.info("Scaling mob spawn rate: {}", mobsToSpawn.get());
                }
            }

            // End invasion if the total duration is reached
            if (invasionDuration.incrementAndGet() * spawnInterval >= maxInvasionDuration) {
                scheduler.shutdown();
                MessageUtils.sendMessageToAllPlayers(world, "Invasion has ended!");

                // Save the scaling data
                ScalingDataUtils.saveScalingData(worldName, mobsToSpawn.get());

                // Update UI to show invasion is over

                if (debugLoggingEnabled) {
                    LOGGER.info("Invasion ended after {} seconds", maxInvasionDuration);
                }
            }

        }, spawnInterval, spawnInterval, TimeUnit.SECONDS);
    }
}