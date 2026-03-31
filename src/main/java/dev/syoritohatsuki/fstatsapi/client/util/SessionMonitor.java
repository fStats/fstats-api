package dev.syoritohatsuki.fstatsapi.client.util;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import net.minecraft.client.Minecraft;

import java.util.UUID;

public final class SessionMonitor {

    private static boolean currentStatus = false;

    public static Boolean isOnline() {
        final var session = Minecraft.getInstance().getUser();
        final var uuidString = UUID.randomUUID().toString();

        final var sessionService = (YggdrasilMinecraftSessionService) Minecraft.getInstance().services().sessionService();
        try {
            sessionService.joinServer(session.getProfileId(), session.getAccessToken(), uuidString);
            if (sessionService.hasJoinedServer(session.getName(), uuidString, null) != null) {
                currentStatus = true;
            }
        } catch (AuthenticationException ignored) {
            currentStatus = false;
        }
        return currentStatus;
    }
}
