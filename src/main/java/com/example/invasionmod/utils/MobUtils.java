package com.example.invasionmod.utils;

import com.example.invasionmod.config.Config;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Zombie;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MobUtils {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final EntityType<? extends Mob> BABYZOMBI = EntityType.ZOMBIE;

    // Dynamic mob weights using configuration
    private static Map<EntityType<? extends Mob>, Integer> getMobWeights() {
        Map<EntityType<? extends Mob>, Integer> dynamicWeights = new HashMap<>() {{
            put(EntityType.ZOMBIE, 10);
            put(BABYZOMBI, 5);
            put(EntityType.SKELETON, 8);
            put(EntityType.CREEPER, 6);
            put(EntityType.SPIDER, 9);
            put(EntityType.ENDERMAN, 5);
            put(EntityType.WITCH, 4);
            put(EntityType.PIGLIN, 6);
            put(EntityType.HUSK, 7);
            put(EntityType.STRAY, 6);
            put(EntityType.PHANTOM, 3);
            put(EntityType.VEX, 2);
            put(EntityType.VINDICATOR, 2);
            put(EntityType.EVOKER, 2);
            put(EntityType.RAVAGER, 1);
            put(EntityType.PILLAGER, 5);
            put(EntityType.WITHER_SKELETON, 2);
            put(EntityType.ZOMBIFIED_PIGLIN, 5);
            put(EntityType.ZOGLIN, 3);
            put(EntityType.HOGLIN, 3);
            put(EntityType.PIGLIN_BRUTE, 2);
            put(EntityType.STRIDER, 6);
            put(EntityType.GHAST, 3);
            put(EntityType.BLAZE, 2);
            put(EntityType.MAGMA_CUBE, 4);
            put(EntityType.SLIME, 7);
            put(EntityType.WITHER, 1);
            put(EntityType.GUARDIAN, 4);
            put(EntityType.ELDER_GUARDIAN, 1);
            put(EntityType.SHULKER, 3);
            put(EntityType.ILLUSIONER, 2);
            put(EntityType.CAVE_SPIDER, 4);
            put(EntityType.DROWNED, 5);
            put(EntityType.ENDERMITE, 3);
            put(EntityType.GIANT, 1);
            put(EntityType.IRON_GOLEM, 8);
            put(EntityType.ENDER_DRAGON, 1);
        }};

        // Optional debug logging
        if (Config.isDebugLoggingEnabled()) {
            LOGGER.info("Current Mob Weights:");
            dynamicWeights.forEach((mob, weight) ->
                    LOGGER.info("{}: {}", mob.getDescriptionId(), weight)
            );
        }

        return dynamicWeights;
    }

    public static void spawnRandomMob(ServerLevel world, Random random) {
        List<ServerPlayer> players = world.players();
        if (players.isEmpty()) return;

        // Get dynamic mob weights each time
        Map<EntityType<? extends Mob>, Integer> mobWeights = getMobWeights();

        EntityType<? extends Mob> randomMobType = getRandomMob(random, mobWeights);

        ServerPlayer player = players.get(random.nextInt(players.size()));
        BlockPos pos = getRandomPositionNearPlayer(player, random);

        Mob mob = randomMobType.create(world);

        if (mob != null) {
            if (randomMobType == BABYZOMBI && mob instanceof Zombie) {
                ((Zombie) mob).setBaby(true);
            }
            mob.moveTo(pos.getX(), pos.getY(), pos.getZ(), world.random.nextFloat() * 360.0F, 0.0F);
            world.addFreshEntity(mob);

            // Optional debug logging
            if (Config.isDebugLoggingEnabled()) {
                LOGGER.info("Spawned {} at {}", randomMobType.getDescriptionId(), pos);
            }
        }
    }

    private static EntityType<? extends Mob> getRandomMob(Random random, Map<EntityType<? extends Mob>, Integer> mobWeights) {
        int totalWeight = mobWeights.values().stream().mapToInt(Integer::intValue).sum();
        int randomWeight = random.nextInt(totalWeight);

        int weightSum = 0;
        for (Map.Entry<EntityType<? extends Mob>, Integer> entry : mobWeights.entrySet()) {
            weightSum += entry.getValue();
            if (randomWeight < weightSum) {
                return entry.getKey();
            }
        }
        return EntityType.ZOMBIE;  // Default to Zombie if something goes wrong
    }

    private static BlockPos getRandomPositionNearPlayer(ServerPlayer player, Random random) {
        int x = player.getBlockX() + random.nextInt(20) - 10;
        int y = player.getBlockY();
        int z = player.getBlockZ() + random.nextInt(20) - 10;
        return new BlockPos(x, y, z);
    }
}
