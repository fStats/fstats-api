package dev.syoritohatsuki.fstatsapi.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class ConfirmMailScreen extends ConfirmScreen {
    private static final Text COPY = Text.translatable("chat.copy");
    private static final Text WARNING = Text.literal("Never send mail to people that you don't trust!");
    private final String mail;
    private final boolean drawWarning;

    public ConfirmMailScreen(BooleanConsumer callback, String mail, boolean mailTrusted) {
        this(callback, getConfirmText(mailTrusted), Text.literal(mail), mail, mailTrusted ? ScreenTexts.CANCEL : ScreenTexts.NO, mailTrusted);
    }

    public ConfirmMailScreen(BooleanConsumer callback, Text title, Text message, String mail, Text noText, boolean mailTrusted) {
        super(callback, title, message);
        this.yesText = mailTrusted ? Text.literal("Open in Mail App") : ScreenTexts.YES;
        this.noText = noText;
        this.drawWarning = !mailTrusted;
        this.mail = mail;
    }

    protected static MutableText getConfirmText(boolean mailTrusted, String mail) {
        return getConfirmText(mailTrusted).append(ScreenTexts.SPACE).append(Text.literal(mail));
    }

    protected static MutableText getConfirmText(boolean mailTrusted) {
        return Text.translatable(mailTrusted ? "Do you want to open this mail or copy it to your clipboard?" : "Are you sure you want to open the following mail?");
    }

    @Override
    protected void addButtons(int y) {
        this.addDrawableChild(ButtonWidget.builder(this.yesText, button -> this.callback.accept(true)).dimensions(this.width / 2 - 50 - 105, y, 100, 20).build());
        this.addDrawableChild(ButtonWidget.builder(COPY, button -> {
            this.client.keyboard.setClipboard(this.mail);
            this.callback.accept(false);
        }).dimensions(this.width / 2 - 50, y, 100, 20).build());
        this.addDrawableChild(ButtonWidget.builder(this.noText, button -> this.callback.accept(false)).dimensions(this.width / 2 - 50 + 105, y, 100, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (this.drawWarning) context.drawCenteredTextWithShadow(this.textRenderer, WARNING, this.width / 2, 110, 16764108);
    }

    public static void open(String mail) {
        Util.getOperatingSystem().open("mailto:" + mail);
    }
}