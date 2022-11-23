package dev.syoritohatsuki.fstatsapi;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class FStatsApiMain implements ModInitializer {
    @Override
    public void onInitialize() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client ->
                FStatsApi.INSTANCE.sendClientData(client, 1, "fstatsapi")
        );

        ServerLifecycleEvents.SERVER_STARTED.register(server ->
                FStatsApi.INSTANCE.sendServerData(server, 1, "fstatsapi")
        );
    }
}