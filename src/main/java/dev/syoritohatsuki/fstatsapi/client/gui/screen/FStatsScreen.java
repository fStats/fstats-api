package dev.syoritohatsuki.fstatsapi.client.gui.screen;

import dev.syoritohatsuki.fstatsapi.FStatsApi;
import dev.syoritohatsuki.fstatsapi.client.util.TextsWithFallbacks;
import dev.syoritohatsuki.fstatsapi.config.Config.Mode;
import dev.syoritohatsuki.fstatsapi.config.ConfigManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class FStatsScreen extends Screen {
    private static final int MARGIN = 8;

    private static final Text DESCRIPTION_TEXT = TextsWithFallbacks.DESCRIPTION_TEXT.formatted(Formatting.GRAY);

    private static final String DEVELOPER_MAIL = "kit.lehto.d@gmail.com";

    private Mode mode = Objects.requireNonNullElse(ConfigManager.read().getMode(), Mode.ALL);
    private final Screen parent;
    private double scroll;

    public FStatsScreen(Screen parent) {
        super(TextsWithFallbacks.TITLE_TEXT);
        this.parent = parent;
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(super.getNarratedTitle(), DESCRIPTION_TEXT);
    }

    @Override
    protected void init() {
        if (client == null) return;

        SimplePositioningWidget simplePositioningWidget = new SimplePositioningWidget();
        simplePositioningWidget.getMainPositioner().margin(MARGIN);
        simplePositioningWidget.setMinHeight(this.height);
        GridWidget gridWidget = simplePositioningWidget.add(new GridWidget(), simplePositioningWidget.copyPositioner().relative(0.5F, 0.0F));
        gridWidget.getMainPositioner().alignHorizontalCenter().marginBottom(MARGIN);
        GridWidget.Adder adder = gridWidget.createAdder(1);
        adder.add(new TextWidget(this.getTitle().copy().formatted(Formatting.YELLOW), this.textRenderer));
        adder.add(new MultilineTextWidget(DESCRIPTION_TEXT, this.textRenderer).setMaxWidth(this.width - MARGIN * 2).setCentered(true));
        GridWidget gridWidget2 = this.createButtonRow(ButtonWidget.builder(TextsWithFallbacks.CONTACT_DEVELOPER_TEXT, button -> this.client.setScreen(new ConfirmMailScreen(confirmed -> {
            if (confirmed) ConfirmMailScreen.open(DEVELOPER_MAIL);
            this.client.setScreen(this);
        }, DEVELOPER_MAIL, true))).build(), ButtonWidget.builder(TextsWithFallbacks.OFFICIAL_PAGE_TEXT, button -> this.client.setScreen(new ConfirmLinkScreen(confirmed -> {
            if (confirmed) Util.getOperatingSystem().open(FStatsApi.OFFICIAL_PAGE_URL);
            this.client.setScreen(this);
        }, FStatsApi.OFFICIAL_PAGE_URL, true))).build());
        adder.add(gridWidget2);
        GridWidget gridWidget3 = this.createButtonRow(
                CyclingButtonWidget.builder((Mode value) -> Text.literal(value.toString()).formatted(switch (value) {
                            case ALL -> Formatting.GREEN;
                            case WITHOUT_LOCATION -> Formatting.YELLOW;
                            case NOTHING -> Formatting.RED;
                        }))
                        .values(Mode.values())
                        .initially(mode)
                        .build(this.width / 2 - 155, 100, 150, 20, TextsWithFallbacks.COLLECT_MODE_TEXT, (button, mode) -> {
                            this.mode = mode;
                            switch (mode) {
                                case ALL -> ConfigManager.enable();
                                case WITHOUT_LOCATION -> ConfigManager.enableWithoutLocation();
                                case NOTHING -> ConfigManager.disable();
                            }
                        }),
                ButtonWidget.builder(ScreenTexts.TO_TITLE, button -> close()).build()
        );
        simplePositioningWidget.add(gridWidget3, simplePositioningWidget.copyPositioner().relative(0.5F, 1.0F));
        simplePositioningWidget.refreshPositions();
        FStatsWidget telemetryEventWidget = new FStatsWidget(0, 0, this.width - 40, gridWidget3.getY() - (gridWidget2.getY() + gridWidget2.getHeight()) - MARGIN * 2, this.client.textRenderer);
        telemetryEventWidget.setMode(mode);
        telemetryEventWidget.setScrollY(this.scroll);
        telemetryEventWidget.setScrollConsumer(scroll -> this.scroll = scroll);
        this.setInitialFocus(telemetryEventWidget);
        adder.add(telemetryEventWidget);
        simplePositioningWidget.refreshPositions();
        SimplePositioningWidget.setPos(simplePositioningWidget, 0, 0, this.width, this.height, 0.5F, 0.0F);
        simplePositioningWidget.forEachChild(this::addDrawableChild);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(context);
        super.render(context, mouseX, mouseY, delta);
    }

    private GridWidget createButtonRow(ClickableWidget... button) {
        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().alignHorizontalCenter().marginX(MARGIN / 2);
        for (int i = 0; i < button.length; i++) gridWidget.add(button[i], 0, i);
        return gridWidget;
    }
}

