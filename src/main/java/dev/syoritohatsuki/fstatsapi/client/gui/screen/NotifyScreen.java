package dev.syoritohatsuki.fstatsapi.client.gui.screen;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.TelemetryEventWidget;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.util.Set;
import java.util.stream.Collectors;

public class NotifyScreen extends Screen {
    private final Set<String> mods = FabricLoader.getInstance().getAllMods().stream().filter(modContainer -> modContainer.getMetadata().containsCustomValue("fstats")).map(modContainer -> modContainer.getMetadata().getName()).collect(Collectors.toSet());
    private final Screen parent;

    private static final Text DESCRIPTION_TEXT = Text.translatable("telemetry_info.screen.description").formatted(Formatting.GRAY);
    private static final Text GIVE_FEEDBACK_TEXT = Text.translatable("telemetry_info.button.give_feedback");
    private static final Text SHOW_DATA_TEXT = Text.translatable("telemetry_info.button.show_data");

    private TelemetryEventWidget telemetryEventWidget;
    private double scroll;

    public NotifyScreen(Screen parent) {
        super(Text.literal("fStats"));
        this.parent = parent;

        mods.add("FabricAPI");
        mods.add("Random Test");
        mods.add("Nansensu");
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        SimplePositioningWidget simplePositioningWidget = new SimplePositioningWidget();
        simplePositioningWidget.getMainPositioner().margin(8);
        simplePositioningWidget.setMinHeight(this.height);
        GridWidget gridWidget = simplePositioningWidget.add(new GridWidget(), simplePositioningWidget.copyPositioner().relative(0.5F, 0.0F));
        gridWidget.getMainPositioner().alignHorizontalCenter().marginBottom(8);
        GridWidget.Adder adder = gridWidget.createAdder(1);
        adder.add(new TextWidget(this.getTitle(), this.textRenderer));
        adder.add(new MultilineTextWidget(DESCRIPTION_TEXT, this.textRenderer).setMaxWidth(this.width - 16).setCentered(true));
        GridWidget gridWidget2 = this.createButtonRow(
                ButtonWidget.builder(GIVE_FEEDBACK_TEXT, this::openFeedbackPage).build(), ButtonWidget.builder(SHOW_DATA_TEXT, button -> {
                }).build()
        );
        adder.add(gridWidget2);
        GridWidget gridWidget3 = this.createButtonRow(ButtonWidget.builder(ScreenTexts.DONE, button -> {
        }).build(), ButtonWidget.builder(ScreenTexts.DONE, button -> {
        }).build());
        simplePositioningWidget.add(gridWidget3, simplePositioningWidget.copyPositioner().relative(0.5F, 1.0F));
        simplePositioningWidget.refreshPositions();
        this.telemetryEventWidget = new TelemetryEventWidget(
                0, 0, this.width - 40, gridWidget3.getY() - (gridWidget2.getY() + gridWidget2.getHeight()) - 16, this.textRenderer
        );
        this.telemetryEventWidget.setScrollY(this.scroll);
        this.telemetryEventWidget.setScrollConsumer(scroll -> this.scroll = scroll);
        this.setInitialFocus(this.telemetryEventWidget);
        adder.add(this.telemetryEventWidget);
        simplePositioningWidget.refreshPositions();
        SimplePositioningWidget.setPos(simplePositioningWidget, 0, 0, this.width, this.height, 0.5F, 0.0F);
        simplePositioningWidget.forEachChild(child -> {
        });
    }

    private void openFeedbackPage(ButtonWidget button) {
        this.client.setScreen(new ConfirmLinkScreen(confirmed -> {
            if (confirmed) {
                Util.getOperatingSystem().open("https://aka.ms/javafeedback?ref=game");
            }

            this.client.setScreen(this);
        }, "https://aka.ms/javafeedback?ref=game", true));
    }

    private GridWidget createButtonRow(ClickableWidget left, ClickableWidget right) {
        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().alignHorizontalCenter().marginX(4);
        gridWidget.add(left, 0, 0);
        gridWidget.add(right, 0, 1);
        return gridWidget;
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }
}
