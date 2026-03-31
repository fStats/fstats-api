package dev.syoritohatsuki.fstatsapi.mixin.client;

import dev.syoritohatsuki.fstatsapi.FStatsApi;
import dev.syoritohatsuki.fstatsapi.client.gui.screen.FStatsScreen;
import dev.syoritohatsuki.fstatsapi.config.ConfigManager;
import dev.syoritohatsuki.fstatsapi.logs.LogManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Function;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "addInitialScreens", at = @At(value = "HEAD"))
    private void initConfigManager(List<Function<Runnable, Screen>> screens, CallbackInfoReturnable<Boolean> cir) {
        LogManager.init();
        if (!ConfigManager.configExists()) {
            screens.add(_ -> new FStatsScreen(new TitleScreen()));
        }
    }

    @Inject(method = "destroy", at = @At(value = "HEAD"))
    private void shutdownFStatsScheduler(CallbackInfo ci) {
        FStatsApi.getScheduler().shutdown();
    }
}
