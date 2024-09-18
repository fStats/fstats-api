package dev.syoritohatsuki.fstatsapi.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.function.DoubleConsumer;

@Environment(EnvType.CLIENT)
public class FStatsWidget extends ScrollableWidget {
    private static final int MARGIN_X = 32;
    private static final Text PROPERTY_TITLE_TEXT = Text.translatable("telemetry_info.property_title").formatted(Formatting.UNDERLINE);
    private final TextRenderer textRenderer;
    private FStatsWidget.Contents contents;
    @Nullable
    private DoubleConsumer scrollConsumer;

    public FStatsWidget(int x, int y, int width, int height, TextRenderer textRenderer) {
        super(x, y, width, height, Text.empty());
        this.textRenderer = textRenderer;
        this.contents = this.collectContents();
    }

    private FStatsWidget.Contents collectContents() {
        FStatsWidget.ContentsBuilder contentsBuilder = new FStatsWidget.ContentsBuilder(this.getGridWidth());

        this.appendCollectableInfo(contentsBuilder);
        contentsBuilder.appendSpace(9);
        this.appendModsList(contentsBuilder);

        return contentsBuilder.build();
    }

    public void setScrollConsumer(@Nullable DoubleConsumer scrollConsumer) {
        this.scrollConsumer = scrollConsumer;
    }

    @Override
    protected void setScrollY(double scrollY) {
        super.setScrollY(scrollY);
        if (this.scrollConsumer != null) {
            this.scrollConsumer.accept(this.getScrollY());
        }
    }

    @Override
    protected int getContentsHeight() {
        return this.contents.grid().getHeight();
    }

    @Override
    protected double getDeltaYPerScroll() {
        return 9.0;
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        int i = this.getY() + this.getPadding();
        int j = this.getX() + this.getPadding();
        context.getMatrices().push();
        context.getMatrices().translate((double) j, (double) i, 0.0);
        this.contents.grid().forEachChild(widget -> widget.render(context, mouseX, mouseY, delta));
        context.getMatrices().pop();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.contents.narration());
    }

    private void appendCollectableInfo(FStatsWidget.ContentsBuilder builder) {
        builder.appendText(this.textRenderer, Text.literal("After entering world"));
        builder.appendText(this.textRenderer, Text.literal("Next data will be collected anonymously after you spawn in world every").formatted(Formatting.GRAY));
        builder.appendSpace(9 / 2);
        builder.appendTitle(this.textRenderer, PROPERTY_TITLE_TEXT, 2);
        builder.appendTitle(this.textRenderer, Text.literal(" - Country name").append(Text.literal(" (If allowed)").formatted(Formatting.GRAY)));
        builder.appendTitle(this.textRenderer, Text.literal(" - Fabric API version").append(Text.literal(" (If installed)").formatted(Formatting.GRAY)));
        builder.appendTitle(this.textRenderer, Text.literal(" - Minecraft version"));
        builder.appendTitle(this.textRenderer, Text.literal(" - Mod version"));
        builder.appendTitle(this.textRenderer, Text.literal(" - Operation System ").append(Text.literal(" (First letter)").formatted(Formatting.GRAY)));
        builder.appendTitle(this.textRenderer, Text.literal(" - Server online mode"));
    }

    private void appendModsList(FStatsWidget.ContentsBuilder builder) {
        builder.appendText(this.textRenderer, Text.literal("List of mods that use fStats"));
        builder.appendSpace(9 / 2);
        FabricLoader.getInstance()
                .getAllMods()
                .stream()
                .filter(modContainer -> modContainer.getMetadata().getCustomValue("fstats") != null)
                .forEach(modContainer -> builder.appendTitle(this.textRenderer, Text.literal(modContainer.getMetadata().getName())
                        .append(Text.literal(" (" + modContainer.getMetadata().getId() + ")").formatted(Formatting.GRAY))
                ));
    }

    private int getGridWidth() {
        return this.width - this.getPaddingDoubled();
    }

    @Environment(EnvType.CLIENT)
    record Contents(GridWidget grid, Text narration) {
    }

    @Environment(EnvType.CLIENT)
    static class ContentsBuilder {
        private final int gridWidth;
        private final GridWidget grid;
        private final GridWidget.Adder widgetAdder;
        private final Positioner positioner;
        private final MutableText narration = Text.empty();

        public ContentsBuilder(int gridWidth) {
            this.gridWidth = gridWidth;
            this.grid = new GridWidget();
            this.grid.getMainPositioner().alignLeft();
            this.widgetAdder = this.grid.createAdder(1);
            this.widgetAdder.add(EmptyWidget.ofWidth(gridWidth));
            this.positioner = this.widgetAdder.copyPositioner().alignHorizontalCenter().marginX(MARGIN_X);
        }

        public void appendTitle(TextRenderer textRenderer, Text title) {
            this.appendTitle(textRenderer, title, 0);
        }

        public void appendTitle(TextRenderer textRenderer, Text title, int marginBottom) {
            this.widgetAdder.add(new MultilineTextWidget(title, textRenderer).setMaxWidth(this.gridWidth), this.widgetAdder.copyPositioner().marginBottom(marginBottom));
            this.narration.append(title).append("\n");
        }

        public void appendText(TextRenderer textRenderer, Text text) {
            this.widgetAdder.add(new MultilineTextWidget(text, textRenderer).setMaxWidth(this.gridWidth - 64).setCentered(true), this.positioner);
            this.narration.append(text).append("\n");
        }

        public void appendSpace(int height) {
            this.widgetAdder.add(EmptyWidget.ofHeight(height));
        }

        public FStatsWidget.Contents build() {
            this.grid.refreshPositions();
            return new FStatsWidget.Contents(this.grid, this.narration);
        }
    }
}