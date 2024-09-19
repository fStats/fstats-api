package dev.syoritohatsuki.fstatsapi.mixin.client;

import dev.syoritohatsuki.fstatsapi.FStatsApi;
import dev.syoritohatsuki.fstatsapi.client.gui.screen.FStatsScreen;
import dev.syoritohatsuki.fstatsapi.client.util.TextsWithFallbacks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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
    private static Identifier FSTATS_TEXTURE = Identifier.of(FStatsApi.MOD_ID, "textures/gui/fstats.png");

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
    private void addFStatsButton(CallbackInfo ci) {
        if (client == null) return;

        if (client.getResourceManager().getResource(FSTATS_TEXTURE).isPresent()) {
            addDrawableChild(
                    new TexturedButtonWidget(
                            this.width / 2 + 104,
                            this.height / 4 + 48,
                            20,
                            20,
                            0,
                            0,
                            0,
                            FSTATS_TEXTURE,
                            20,
                            20,
                            button -> client.setScreen(new FStatsScreen(this)),
                            TextsWithFallbacks.MOD_NAME_TEXT
                    )
            );
        } else {
            addDrawableChild(
                    ButtonWidget.builder(TextsWithFallbacks.MOD_NAME_TEXT, button -> this.client.setScreen(new FStatsScreen(this)))
                            .dimensions(this.width / 2 + 104, this.height / 4 + 48, 42, 20)
                            .build()
            );
        }
    }
}
