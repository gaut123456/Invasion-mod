package com.example.invasionmod.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    // Configuration parameters
    private static final ForgeConfigSpec.ConfigValue<Integer> spawnInterval;
    private static final ForgeConfigSpec.ConfigValue<Integer> scaleInterval;
    private static final ForgeConfigSpec.ConfigValue<Integer> initialMobsPerSpawn;
    private static final ForgeConfigSpec.ConfigValue<Integer> scalingMobsPerInterval;
    private static final ForgeConfigSpec.ConfigValue<Integer> invasionDuration;
    private static final ForgeConfigSpec.ConfigValue<Boolean> debugLoggingEnabled;

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        // Defining config values with default values
        builder.push("invasion_settings");
        spawnInterval = builder.comment("Interval between mob spawns in seconds (e.g., 10 seconds)")
                .defineInRange("spawnInterval", 10, 1, 60);
        scaleInterval = builder.comment("Interval in seconds after which the spawn count increases (e.g., 30 seconds)")
                .defineInRange("scaleInterval", 30, 1, 300);
        initialMobsPerSpawn = builder.comment("Initial number of mobs to spawn every spawnInterval")
                .defineInRange("initialMobsPerSpawn", 1, 1, 100);
        scalingMobsPerInterval = builder.comment("Number of mobs to add every scaleInterval")
                .defineInRange("scalingMobsPerInterval", 1, 1, 10);
        invasionDuration = builder.comment("Duration of the invasion in seconds")
                .defineInRange("invasionDuration", 300, 60, 99999999);
        debugLoggingEnabled = builder.comment("Enable debug logging for invasion events")
                .define("debugLoggingEnabled", false);
        builder.pop();

        // Register the config
        SPEC = builder.build();
    }

    public static ForgeConfigSpec SPEC;

    // Accessor methods for the config values
    public static int getSpawnInterval() {
        return spawnInterval.get();
    }

    public static int getScaleInterval() {
        return scaleInterval.get();
    }

    public static int getInitialMobsPerSpawn() {
        return initialMobsPerSpawn.get();
    }

    public static int getScalingMobsPerInterval() {
        return scalingMobsPerInterval.get();
    }

    public static int getInvasionDuration() {
        return invasionDuration.get();
    }

    public static boolean isDebugLoggingEnabled() {
        return debugLoggingEnabled.get();
    }
}
