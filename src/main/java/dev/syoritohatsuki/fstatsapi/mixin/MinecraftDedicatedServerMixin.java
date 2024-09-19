package dev.syoritohatsuki.fstatsapi.mixin;

import dev.syoritohatsuki.fstatsapi.FStatsApi;
import dev.syoritohatsuki.fstatsapi.config.ConfigManager;
import dev.syoritohatsuki.fstatsapi.logs.LogManager;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftDedicatedServer.class)
public abstract class MinecraftDedicatedServerMixin {

    @Inject(method = "setupServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/MinecraftDedicatedServer;loadWorld()V", shift = At.Shift.AFTER))
    private void afterSetupServer(CallbackInfoReturnable<Boolean> cir) {

        LogManager.init();

        if (!ConfigManager.read().isEnabled()) return;

        LogManager.logger.info("-----[ \u001B[36m\u001B[1mfStats\u001B[0m ]-----");
        LogManager.logger.info("\u001B[33mOn server exist mod that collect metric data about mods usage for few author\u001B[0m");
        LogManager.logger.info("\u001B[33mThat mod work like bStats, no need to panic. You can disable it config if you want\u001B[0m");
        LogManager.logger.info("--------------------");

        FStatsApi.sendMetricRequest();
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void onShutdown(CallbackInfo ci) {
        LogManager.logger.info("Stopping fStats");
        FStatsApi.getScheduler().shutdown();
    }
}
