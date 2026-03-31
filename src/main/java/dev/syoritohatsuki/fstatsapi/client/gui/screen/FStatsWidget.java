package dev.syoritohatsuki.fstatsapi.client.gui.screen;

import dev.syoritohatsuki.fstatsapi.client.util.TextsWithFallbacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.AbstractTextAreaWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.function.DoubleConsumer;

import static dev.syoritohatsuki.fstatsapi.client.util.TextsWithFallbacks.*;

@Environment(EnvType.CLIENT)
public class FStatsWidget extends AbstractTextAreaWidget {
    private static final Component PROPERTY_TITLE_TEXT = Component.translatable("telemetry_info.property_title").withStyle(ChatFormatting.UNDERLINE);
    private final Font textRenderer;
    private FStatsWidget.Content contents;
    @Nullable
    private DoubleConsumer scrollConsumer;

    public FStatsWidget(int x, int y, int width, int height, Font textRenderer) {
        super(x, y, width, height, Component.empty(), AbstractScrollArea.defaultSettings(9));
        this.textRenderer = textRenderer;
        this.contents = this.collectContents();
    }

    public void updateLayout() {
        this.contents = this.collectContents();
        this.refreshScrollAmount();
    }

    private FStatsWidget.Content collectContents() {
        FStatsWidget.ContentsBuilder contentsBuilder = new FStatsWidget.ContentsBuilder(this.getGridWidth());

        this.appendCollectableInfo(contentsBuilder);
        contentsBuilder.addSpacer(9);
        this.appendModsList(contentsBuilder);

        return contentsBuilder.build();
    }

    public void setScrollConsumer(@Nullable DoubleConsumer scrollConsumer) {
        this.scrollConsumer = scrollConsumer;
    }

    @Override
    public void setScrollAmount(double scrollY) {
        super.setScrollAmount(scrollY);
        if (this.scrollConsumer != null) {
            this.scrollConsumer.accept(this.scrollAmount());
        }
    }

    @Override
    protected int getInnerHeight() {
        return this.contents.container().getHeight();
    }

    private int getGridWidth() {
        return this.width - this.totalInnerPadding();
    }


    @Override
    protected void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        int top = this.getInnerTop();
        int left = this.getInnerLeft();
        graphics.pose().pushMatrix();
        graphics.pose().translate(left, top);
        this.contents.container().visitWidgets(widget -> widget.extractRenderState(graphics, mouseX, mouseY, a));
        graphics.pose().popMatrix();
    }

    @Override
    protected void updateWidgetNarration(final NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, this.contents.narration());
    }

    private void appendCollectableInfo(FStatsWidget.ContentsBuilder builder) {
        builder.addHeader(this.textRenderer, TextsWithFallbacks.WORLD_EVENT_TEXT);
        builder.addHeader(this.textRenderer, TextsWithFallbacks.WORLD_EVENT_DESCRIPTION_TEXT.withStyle(ChatFormatting.GRAY));
        builder.addSpacer(9 / 2);
        builder.addLine(this.textRenderer, PROPERTY_TITLE_TEXT, 2);
        builder.addLine(this.textRenderer, Component.literal(" - ")
                .append(TextsWithFallbacks.COUNTRY_CODE_TEXT.withStyle(ChatFormatting.BLUE))
                .append(CommonComponents.SPACE)
                .append(Component.literal("(")
                        .append(IF_ALLOWED_TEXT)
                        .append(Component.literal(")"))
                        .withStyle(ChatFormatting.DARK_GRAY)
                )
        );
        builder.addLine(this.textRenderer, Component.literal(" - ")
                .append(TextsWithFallbacks.FABRIC_API_VERSION_TEXT.withStyle(ChatFormatting.BLUE))
                .append(CommonComponents.SPACE)
                .append(Component.literal("(")
                        .append(IF_INSTALLED_TEXT)
                        .append(Component.literal(")"))
                        .withStyle(ChatFormatting.DARK_GRAY)
                )
        );
        builder.addLine(this.textRenderer, Component.literal(" - ")
                .append(TextsWithFallbacks.MOD_VERSION_TEXT.withStyle(ChatFormatting.BLUE)));
        builder.addLine(this.textRenderer, Component.literal(" - ")
                .append(MINECRAFT_VERSION_TEXT.withStyle(ChatFormatting.BLUE)));
        builder.addLine(this.textRenderer, Component.literal(" - ")
                .append(OPERATION_SYSTEM_TEXT.withStyle(ChatFormatting.BLUE))
                .append(CommonComponents.SPACE)
                .append(Component.literal("(")
                        .append(FIRST_LETTER_TEXT)
                        .append(Component.literal(")"))
                        .withStyle(ChatFormatting.DARK_GRAY)
                )
        );
    }

    private void appendModsList(FStatsWidget.ContentsBuilder builder) {
        builder.addHeader(this.textRenderer, MODS_USE_TEXT);
        builder.addSpacer(9 / 2);
        FabricLoader.getInstance()
                .getAllMods()
                .stream()
                .filter(modContainer -> modContainer.getMetadata().getCustomValue("fstats") != null)
                .forEach(modContainer -> builder.addLine(this.textRenderer, Component.literal(modContainer.getMetadata().getName())
                        .append(Component.literal(" (" + modContainer.getMetadata().getId() + ")").withStyle(ChatFormatting.DARK_GRAY))
                ));
    }

    @Override
    protected int innerPadding() {
        return 8;
    }

    @Environment(EnvType.CLIENT)
    record Content(Layout container, Component narration) {
    }

    @Environment(EnvType.CLIENT)
    static class ContentsBuilder {
        private final int width;
        private final LinearLayout layout;
        private final MutableComponent narration = Component.empty();

        public ContentsBuilder(int width) {
            this.width = width;
            this.layout = LinearLayout.vertical();
            this.layout.defaultCellSetting().alignHorizontallyLeft();
            this.layout.addChild(SpacerElement.width(width));
        }

        public void addLine(final Font font, final Component line) {
            this.addLine(font, line, 0);
        }

        public void addLine(final Font font, final Component line, final int paddingBottom) {
            this.layout.addChild(new MultiLineTextWidget(line, font).setMaxWidth(this.width), s -> s.paddingBottom(paddingBottom));
            this.narration.append(line).append("\n");
        }

        public void addHeader(final Font font, final Component line) {
            this.layout.addChild(new MultiLineTextWidget(line, font).setMaxWidth(this.width - 64)
                    .setCentered(true), s -> s.alignHorizontallyCenter().paddingHorizontal(32));
            this.narration.append(line).append("\n");
        }

        public void addSpacer(final int height) {
            this.layout.addChild(SpacerElement.height(height));
        }

        public FStatsWidget.Content build() {
            this.layout.arrangeElements();
            return new FStatsWidget.Content(this.layout, this.narration);
        }
    }
}
