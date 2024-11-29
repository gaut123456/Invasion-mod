package com.example.invasionmod.mobs;

import com.example.invasionmod.utils.MessageUtils;
import com.example.invasionmod.utils.MobUtils;
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
                    world.getServer().execute(() -> {
                        MobUtils.spawnRandomMob(world, random);
                    });
                    MessageUtils.sendMessageToAllPlayers(world, "Mob spawned! : " + data.getMobsToSpawn());
                }
            }
            else {
                MessageUtils.sendMessageToAllPlayers(world, "Max mobs to spawn reached!");
            }

            if (invasionDuration.incrementAndGet() >= maxInvasionDuration) {
                scheduler.shutdown();
                MessageUtils.sendMessageToAllPlayers(world, "Invasion has ended!");
            }

        }, 0, 10, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(() -> {
            if (data.getMobsToSpawn() < maxMobsToSpawn) {
                data.setMobsToSpawn(data.getMobsToSpawn() + 1);
            }
        }, 30, 30, TimeUnit.SECONDS);
    }
}