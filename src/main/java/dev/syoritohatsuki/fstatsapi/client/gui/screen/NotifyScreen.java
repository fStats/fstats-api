package dev.syoritohatsuki.fstatsapi.client.gui.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class NotifyScreen extends Screen {
    private final Screen parent;

    private static final Text DESCRIPTION_TEXT = Text.literal("fStats is a 3rd-party metric collection library. The Main idea is help developers to recognize their actual community based on charts").formatted(Formatting.GRAY);
    private double scroll;

    public NotifyScreen(Screen parent) {
        super(Text.literal("fStats"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        SimplePositioningWidget simplePositioningWidget = new SimplePositioningWidget();
        simplePositioningWidget.getMainPositioner().margin(8);
        //noinspection SuspiciousNameCombination
        simplePositioningWidget.setMinHeight(height);
        GridWidget gridWidget = simplePositioningWidget.add(new GridWidget(), simplePositioningWidget.copyPositioner().relative(0.5F, 0.0F));
        gridWidget.getMainPositioner().alignHorizontalCenter().marginBottom(8);
        GridWidget.Adder adder = gridWidget.createAdder(1);
        adder.add(new TextWidget(getTitle(), textRenderer));
        adder.add(new MultilineTextWidget(DESCRIPTION_TEXT, textRenderer).setMaxWidth(width - 16).setCentered(true));
        GridWidget gridWidget2 = createButtonRow(
                ButtonWidget.builder(Text.literal("fdsfsdf"), button -> {
                }).build(), ButtonWidget.builder(Text.literal("rqrqrqr"), button -> {
                }).build()
        );
        adder.add(gridWidget2);
        GridWidget gridWidget3 = createButtonRow(ButtonWidget.builder(ScreenTexts.CANCEL, button -> {
        }).build(), ButtonWidget.builder(ScreenTexts.DONE, button -> close()).build());
        simplePositioningWidget.add(gridWidget3, simplePositioningWidget.copyPositioner().relative(0.5F, 1.0F));
        simplePositioningWidget.refreshPositions();
        ModsWidget modsWidget = new ModsWidget(
                0, 0, width - 40, gridWidget3.getY() - (gridWidget2.getY() + gridWidget2.getHeight()) - 16, textRenderer
        );
        modsWidget.setScrollY(scroll);
        modsWidget.setScrollConsumer(scroll -> this.scroll = scroll);
        setInitialFocus(modsWidget);
        adder.add(modsWidget);
        simplePositioningWidget.refreshPositions();
        SimplePositioningWidget.setPos(simplePositioningWidget, 0, 0, width, height, 0.5F, 0.0F);
        simplePositioningWidget.forEachChild(this::addDrawableChild);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
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
