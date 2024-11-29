package com.example.invasionmod.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class MessageUtils {
    public static void sendMessageToPlayer(ServerPlayer player, String message) {
        player.sendSystemMessage(Component.literal(message));
    }

    public static void sendMessageToAllPlayers(ServerLevel world, String message) {
        for (ServerPlayer player : world.players()) {
            sendMessageToPlayer(player, message);
        }
    }
}
