package dev.syoritohatsuki.fstatsapi.client.util;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import net.minecraft.client.MinecraftClient;

import java.util.UUID;

public final class SessionMonitor {

    private static boolean currentStatus = false;

    public static Boolean isOnline() {
        final var session = MinecraftClient.getInstance().getSession();
        final var uuidString = UUID.randomUUID().toString();

        final var sessionService = (YggdrasilMinecraftSessionService) MinecraftClient.getInstance().getSessionService();
        try {
            sessionService.joinServer(session.getUuidOrNull(), session.getAccessToken(), uuidString);
            if (sessionService.hasJoinedServer(session.getUsername(), uuidString, null) != null) {
                currentStatus = true;
            }
        } catch (AuthenticationException ignored) {
            currentStatus = false;
        }
        return currentStatus;
    }
}
