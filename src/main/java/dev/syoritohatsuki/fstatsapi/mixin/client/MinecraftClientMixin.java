package dev.syoritohatsuki.fstatsapi.mixin.client;

import dev.syoritohatsuki.fstatsapi.FStatsApi;
import dev.syoritohatsuki.fstatsapi.client.gui.screen.FStatsScreen;
import dev.syoritohatsuki.fstatsapi.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Function;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "createInitScreens", at = @At(value = "HEAD"))
    private void initConfigManager(List<Function<Runnable, Screen>> list, CallbackInfo ci) {
        if (!ConfigManager.configExists()) {
            list.add(onClose -> new FStatsScreen(new TitleScreen()));
        }
    }

    @Inject(method = "stop", at = @At(value = "HEAD"))
    private void shutdownFStatsScheduler(CallbackInfo ci) {
        FStatsApi.getScheduler().shutdown();
    }
}
