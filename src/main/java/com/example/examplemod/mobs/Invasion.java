package com.example.examplemod.mobs;

import com.example.examplemod.utils.MessageUtils;
import com.example.examplemod.utils.MobUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.core.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
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
    private static final int maxMobsToSpawn = 10;
    private static final int maxInvasionDuration = 300; // in seconds
    private final AtomicInteger invasionDuration = new AtomicInteger(0);

    public void startSpawning(ServerLevel world) {
        InvasionData data = InvasionData.get(world); // Load the saved data

        scheduler.scheduleAtFixedRate(() -> {
            if (!invasionStarted) {
                MessageUtils.sendMessageToAllPlayers(world, "Invasion is starting!");
                invasionStarted = true;
            }

            if (data.getMobsToSpawn() <= maxMobsToSpawn) {
                for (int i = 0; i < data.getMobsToSpawn(); i++) {
                    MobUtils.spawnRandomMob(world, random);
                }
            }

            if (invasionDuration.incrementAndGet() >= maxInvasionDuration) {
                scheduler.shutdown();
                MessageUtils.sendMessageToAllPlayers(world, "Invasion has ended!");
            }

        }, 0, 10, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(() -> {
            if (data.getMobsToSpawn() < maxMobsToSpawn) {
                data.setMobsToSpawn(data.getMobsToSpawn() + 1); // Persist the updated count
            }
        }, 30, 30, TimeUnit.SECONDS);
    }
}