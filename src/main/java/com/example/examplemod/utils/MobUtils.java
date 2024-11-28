package com.example.examplemod.utils;

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
    private static final Map<EntityType<? extends Mob>, Integer> mobWeights = new HashMap<>() {{
        put(EntityType.ZOMBIE, 10);
        put(BABZYZOMBI, 9);
        put(EntityType.SKELETON, 10);
        put(EntityType.CREEPER, 8);
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
    private static final EntityType<? extends Mob> BABZYZOMBI = EntityType.ZOMBIE;


    public static void spawnRandomMob(ServerLevel world, Random random) {
        List<ServerPlayer> players = world.players();
        if (players.isEmpty()) return;

        EntityType<? extends Mob> randomMobType = getRandomMob(random);

        ServerPlayer player = players.get(random.nextInt(players.size()));
        BlockPos pos = getRandomPositionNearPlayer(player, random);

        Mob mob = randomMobType.create(world);

        if (mob != null) {
            if (randomMobType == BABZYZOMBI && mob instanceof Zombie) {
                ((Zombie) mob).setBaby(true);
            }
            mob.moveTo(pos.getX(), pos.getY(), pos.getZ(), world.random.nextFloat() * 360.0F, 0.0F);
            world.addFreshEntity(mob);
            LOGGER.info("Spawned {} at {}", randomMobType.getDescriptionId(), pos);
        }
    }

    private static EntityType<? extends Mob> getRandomMob(Random random) {
        int totalWeight = mobWeights.values().stream().mapToInt(Integer::intValue).sum();
        int randomWeight = random.nextInt(totalWeight);

        for (Map.Entry<EntityType<? extends Mob>, Integer> entry : mobWeights.entrySet()) {
            randomWeight -= entry.getValue();
            if (randomWeight < 0) {
                return entry.getKey();
            }
        }
        return EntityType.ZOMBIE;
    }

    private static BlockPos getRandomPositionNearPlayer(ServerPlayer player, Random random) {
        double radius = 1 + random.nextInt(10);
        double angle = random.nextDouble() * 2 * Math.PI;
        double x = player.getX() + radius * Math.cos(angle);
        double y = player.getY();
        double z = player.getZ() + radius * Math.sin(angle);

        return new BlockPos((int) x, (int) y, (int) z);
    }
}
