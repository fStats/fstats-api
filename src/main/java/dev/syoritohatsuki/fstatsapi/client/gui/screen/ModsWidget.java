package dev.syoritohatsuki.fstatsapi.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.util.telemetry.TelemetryEventType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleConsumer;
import java.util.stream.Collectors;

public class ModsWidget extends ScrollableWidget {
    private final Set<String> mods = FabricLoader.getInstance().getAllMods().stream().filter(modContainer -> modContainer.getMetadata().containsCustomValue("fstats")).map(modContainer -> modContainer.getMetadata().getName()).collect(Collectors.toSet());
    private final TextRenderer textRenderer;
    private final Contents contents;
    @Nullable
    private DoubleConsumer scrollConsumer;

    public ModsWidget(int x, int y, int width, int height, TextRenderer textRenderer) {
        super(x, y, width, height, Text.empty());
        mods.addAll(Set.of("FabricAPI", "Nansensu", "Random Test"));
        this.textRenderer = textRenderer;
        this.contents = this.collectContents(MinecraftClient.getInstance().isOptionalTelemetryEnabled());
    }

    private Contents collectContents(boolean optionalTelemetryEnabled) {
        ContentsBuilder contentsBuilder = new ContentsBuilder(this.getGridWidth());
        List<TelemetryEventType> list = new ArrayList<>(TelemetryEventType.getTypes());
        list.sort(Comparator.comparing(TelemetryEventType::isOptional));

        if (!optionalTelemetryEnabled) {
            list.removeIf(TelemetryEventType::isOptional);
        }

        mods.forEach(s -> {
            contentsBuilder.appendTitle(this.textRenderer, Text.literal(s));
            contentsBuilder.appendSpace(9);
        });

        return contentsBuilder.build();
    }

    public void setScrollConsumer(@Nullable DoubleConsumer scrollConsumer) {
        this.scrollConsumer = scrollConsumer;
    }

    @Override
    public void setScrollY(double scrollY) {
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
        context.getMatrices().translate(j, i, 0.0);
        this.contents.grid().forEachChild(widget -> widget.render(context, mouseX, mouseY, delta));
        context.getMatrices().pop();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.contents.narration());
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
            this.positioner = this.widgetAdder.copyPositioner().alignHorizontalCenter().marginX(32);
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

        public Contents build() {
            this.grid.refreshPositions();
            return new Contents(this.grid, this.narration);
        }
    }
}
