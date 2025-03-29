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
        LogManager.logger.info("\u001B[33mThis mod collects \u001B[32manonymous usage data\u001B[33m to help mod authors improve their projects.\u001B[0m");
        LogManager.logger.info("\u001B[33mData collection is enabled by default.\u001B[0m");
        LogManager.logger.info("\u001B[33mYou can disable it at any time by editing the config file,\u001B[0m");
        LogManager.logger.info("\u001B[33mlocated in the same directory as other mod configurations.\u001B[0m");
        LogManager.logger.info("\u001B[32mNo personal data is collected.\u001B[0m \u001B[33mData is used solely for statistical purposes.\u001B[0m");
        LogManager.logger.info("\u001B[33mCollected data:\u001B[0m");
        LogManager.logger.info("\u001B[34m - Country\u001B[0m \u001B[90m(If enabled)\u001B[0m");
        LogManager.logger.info("\u001B[34m - Fabric API version \u001B[90m(If installeed )\u001B[0m");
        LogManager.logger.info("\u001B[34m - Minecraft version\u001B[0m");
        LogManager.logger.info("\u001B[34m - Mod version\u001B[0m");
        LogManager.logger.info("\u001B[34m - OS\u001B[0m \u001B[90m(First letter)\u001B[0m");
        LogManager.logger.info("\u001B[34m - Server online mode\u001B[0m");
        LogManager.logger.info("\u001B[34m - Data source \u001B[90m(client or server)\u001B[0m");
        LogManager.logger.info("--------------------");

        FStatsApi.sendMetricRequest();
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void onShutdown(CallbackInfo ci) {
        LogManager.logger.info("Stopping fStats");
        FStatsApi.getScheduler().shutdown();
    }
}
