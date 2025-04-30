package dev.syoritohatsuki.fstatsapi.client.util;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import net.minecraft.client.MinecraftClient;

import java.util.UUID;

public final class SessionMonitor {

    private static boolean currentStatus = false;

    public static Boolean isOnline() {
        final var session = MinecraftClient.getInstance().getSession();
        final var gameProfile = session.getProfile();
        final var uuidString = UUID.randomUUID().toString();

        final var sessionService = (YggdrasilMinecraftSessionService) MinecraftClient.getInstance().getSessionService();
        try {
            sessionService.joinServer(gameProfile, session.getAccessToken(), uuidString);
            if (sessionService.hasJoinedServer(gameProfile, uuidString, null).isComplete()) {
                currentStatus = true;
            }
        } catch (AuthenticationException ignored) {
            currentStatus = false;
        }
        return currentStatus;
    }
}
