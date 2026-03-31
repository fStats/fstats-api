package dev.syoritohatsuki.fstatsapi.mixin.client;

import dev.syoritohatsuki.fstatsapi.FStatsApi;
import dev.syoritohatsuki.fstatsapi.client.gui.screen.FStatsScreen;
import dev.syoritohatsuki.fstatsapi.client.util.TextsWithFallbacks;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    @Unique
    @Final
    private static Identifier FSTATS_TEXTURE = Identifier.fromNamespaceAndPath(FStatsApi.MOD_ID, "fstats");

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/TitleScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;"))
    private void addFStatsButton(CallbackInfo ci) {
        if (FabricLoader.getInstance().isModLoaded("fabric-api")) {
            addRenderableWidget(
                    new ImageButton(
                            this.width / 2 + 104,
                            this.height / 4 + 48,
                            20,
                            20,
                            new WidgetSprites(FSTATS_TEXTURE, FSTATS_TEXTURE),
                            _ -> minecraft.setScreen(new FStatsScreen(this)),
                            TextsWithFallbacks.MOD_NAME_TEXT
                    )
            );
        } else {
            addRenderableWidget(
                    ImageButton.builder(TextsWithFallbacks.MOD_NAME_TEXT, _ -> minecraft.setScreen(new FStatsScreen(this)))
                            .bounds(this.width / 2 + 104, this.height / 4 + 48, 42, 20)
                            .build()
            );
        }
    }
}
