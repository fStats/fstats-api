package dev.syoritohatsuki.fstatsapi.mixin.client;

import dev.syoritohatsuki.fstatsapi.FStatsApi;
import dev.syoritohatsuki.fstatsapi.client.gui.screen.NotifyScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
    private void addFStatsButton(CallbackInfo ci) {
//        TexturedButtonWidget fStatsIconButtonWidget = addDrawableChild(
//                TexturedButtonWidget.builder(Text.literal("fStats"), button -> this.client.setScreen(new NotifyScreen(() -> client.setScreen(this))), true)
//                        .width(20)
//                        .texture(new Identifier(FStatsApi.MOD_ID, "icon/fstats"), 18, 18)
//                        .build()
//        );
//        fStatsIconButtonWidget.setPosition(this.width / 2 + 128, (this.height / 4 + 48) + 72 + 12);

        addDrawableChild(
                new TexturedButtonWidget(
                        this.width / 2 + 128,
                        (this.height / 4 + 48) + 72 + 12,
                        18,
                        18,
                        0,
                        0,
                        0,
                        new Identifier(FStatsApi.MOD_ID, "textures/gui/fstats.png"),
                        313,
                        313,
                        button -> this.client.setScreen(new NotifyScreen(this)),
                        Text.literal("fStats")
                )
        );
    }
}
