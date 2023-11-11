package dev.syoritohatsuki.fstatsapi.mixin;

import dev.syoritohatsuki.fstatsapi.FStatsApi;
import dev.syoritohatsuki.fstatsapi.config.ConfigManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow
    public abstract String getVersion();

    @Shadow
    public abstract boolean isOnlineMode();

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;createMetadata()Lnet/minecraft/server/ServerMetadata;", ordinal = 0), method = "runServer")
    private void afterSetupServer(CallbackInfo info) {

        FStatsApi.logger.info("-----[ \u001B[36m\u001B[1mfStats\u001B[0m ]-----");
        FStatsApi.logger.info("\u001B[33mOn server exist mod that collect metric data about mods usage for few author\u001B[0m");
        FStatsApi.logger.info("\u001B[33mThat mod work like bStats, no need to panic. U can disable it config if you want\u001B[0m");
        FStatsApi.logger.info("--------------------");

        ConfigManager.init();

        if (ConfigManager.read().enabled()) {
            FStatsApi.sendMetricRequest(getVersion(), isOnlineMode());
        }
    }
}
