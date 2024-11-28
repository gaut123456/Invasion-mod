package com.example.examplemod.mobs;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class InvasionData extends SavedData {
    private static final String DATA_NAME = "invasion_data";
    private int mobsToSpawn = 1;

    public static InvasionData load(CompoundTag tag) {
        InvasionData data = new InvasionData();
        data.mobsToSpawn = tag.getInt("mobsToSpawn");
        return data;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        tag.putInt("mobsToSpawn", mobsToSpawn);
        return tag;
    }

    public int getMobsToSpawn() {
        return mobsToSpawn;
    }

    public void setMobsToSpawn(int mobsToSpawn) {
        this.mobsToSpawn = mobsToSpawn;
        setDirty();
    }

    public static InvasionData get(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(InvasionData::load, InvasionData::new, DATA_NAME);
    }
}
