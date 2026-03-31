package dev.syoritohatsuki.fstatsapi.client.gui.screen;

import dev.syoritohatsuki.fstatsapi.client.util.TextsWithFallbacks;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class ConfirmMailScreen extends ConfirmScreen {
    private static final Component COPY = Component.translatable("chat.copy");
    private final String mail;
    private final boolean showWarning;

    public ConfirmMailScreen(BooleanConsumer callback, Component title, Component message, String mail, Component noText, boolean mailTrusted) {
        super(callback, title, message);
        this.yesButtonComponent = mailTrusted ? TextsWithFallbacks.MAIL_OPEN_TEXT : CommonComponents.GUI_YES;
        this.noButtonComponent = noText;
        this.showWarning = !mailTrusted;
        this.mail = mail;
    }

    @Override
    protected void addAdditionalText() {
        if (this.showWarning) {
            this.layout.addChild(new StringWidget(TextsWithFallbacks.MAIL_WARNING_TEXT, this.font));
        }
    }

    @Override
    protected void addButtons(LinearLayout layout) {
        this.yesButton = layout.addChild(Button.builder(this.yesButtonComponent, _ -> this.callback.accept(true)).width(100).build());
        layout.addChild(Button.builder(COPY, _ -> {
            this.minecraft.keyboardHandler.setClipboard(this.mail);
            this.callback.accept(false);
        }).width(100).build());
        this.noButton = layout.addChild(Button.builder(this.noButtonComponent, _ -> this.callback.accept(false)).width(100).build());
    }

    public static void open(Screen parent, String mail, boolean mailTrusted) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new ConfirmLinkScreen(shouldOpen -> {
            if (shouldOpen) {
                Util.getPlatform().openUri("mailto:" + mail);
            }

            minecraft.setScreen(parent);
        }, "mailto:" + mail, mailTrusted));
    }
}
