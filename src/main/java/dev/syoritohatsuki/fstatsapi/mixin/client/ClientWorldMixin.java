package dev.syoritohatsuki.fstatsapi.mixin.client;

import dev.syoritohatsuki.fstatsapi.FStatsApi;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        if (FStatsApi.getRequestSendingTaskFuture() == null || FStatsApi.getRequestSendingTaskFuture().isCancelled()) {
            FStatsApi.sendMetricRequest();
        }
    }

    @Inject(method = "disconnect", at = @At("TAIL"))
    private void aVoid(CallbackInfo ci) {
        FStatsApi.getRequestSendingTaskFuture().cancel(true);
    }
}
