package dev.syoritohatsuki.fstatsapi.mixin;

import dev.syoritohatsuki.fstatsapi.FStatsApi;
import dev.syoritohatsuki.fstatsapi.config.ConfigManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftDedicatedServer.class)
public abstract class MinecraftDedicatedServerMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/MinecraftDedicatedServer;loadWorld()V", shift = At.Shift.AFTER), method = "setupServer")
    private void afterSetupServer(CallbackInfoReturnable<Boolean> cir) {

        FStatsApi.logger.info("-----[ \u001B[36m\u001B[1mfStats\u001B[0m ]-----");
        FStatsApi.logger.info("\u001B[33mOn server exist mod that collect metric data about mods usage for few author\u001B[0m");
        FStatsApi.logger.info("\u001B[33mThat mod work like bStats, no need to panic. You can disable it config if you want\u001B[0m");
        FStatsApi.logger.info("--------------------");

        ConfigManager.init();

        if (ConfigManager.read().enabled()) {
            FStatsApi.sendMetricRequest(((MinecraftServer) (Object) this).getVersion(), ((MinecraftServer) (Object) this).isOnlineMode());
        }
    }
}