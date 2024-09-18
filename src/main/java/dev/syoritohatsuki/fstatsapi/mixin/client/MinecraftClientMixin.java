package dev.syoritohatsuki.fstatsapi.mixin.client;

import dev.syoritohatsuki.fstatsapi.FStatsApi;
import dev.syoritohatsuki.fstatsapi.client.gui.screen.FStatsScreen;
import dev.syoritohatsuki.fstatsapi.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow public abstract void setScreen(@Nullable Screen screen);

    @Inject(method = "onInitFinished", at = @At(value = "HEAD"), cancellable = true)
    private void initConfigManager(CallbackInfo ci) {
        if (!ConfigManager.configExists()) {
            setScreen(new FStatsScreen(new TitleScreen()));
            ci.cancel();
        }
    }

    @Inject(method = "stop", at = @At(value = "HEAD"))
    private void shutdownFStatsScheduler(CallbackInfo ci) {
        FStatsApi.getScheduler().shutdown();
    }
}
