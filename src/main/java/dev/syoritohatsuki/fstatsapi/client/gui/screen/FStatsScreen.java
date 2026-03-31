package dev.syoritohatsuki.fstatsapi.client.gui.screen;

import dev.syoritohatsuki.fstatsapi.FStatsApi;
import dev.syoritohatsuki.fstatsapi.client.util.TextsWithFallbacks;
import dev.syoritohatsuki.fstatsapi.config.Config.Mode;
import dev.syoritohatsuki.fstatsapi.config.ConfigManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static dev.syoritohatsuki.fstatsapi.config.Config.Mode.*;

@Environment(EnvType.CLIENT)
public class FStatsScreen extends Screen {
    private static final Component TITLE_TEXT = TextsWithFallbacks.TITLE_TEXT.withStyle(ChatFormatting.YELLOW);
    private static final Component DESCRIPTION_TEXT = TextsWithFallbacks.DESCRIPTION_TEXT.withStyle(ChatFormatting.GRAY);
    private static final String DEVELOPER_MAIL = "kit.lehto.d@gmail.com";
    private final Screen parent;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(
            this, 16 + 9 * 5 + 20, 33
    );
    private Mode mode = Objects.requireNonNullElse(ConfigManager.read().getMode(), ALL);
    @Nullable
    private FStatsWidget fStatsWidget;
    @Nullable
    private MultiLineTextWidget description;
    private double savedScroll;

    public FStatsScreen(Screen parent) {
        super(TITLE_TEXT);
        this.parent = parent;
    }

    @Override
    public @NonNull Component getNarrationMessage() {
        return CommonComponents.joinForNarration(super.getNarrationMessage(), DESCRIPTION_TEXT);
    }

    @Override
    protected void init() {
        LinearLayout header = this.layout.addToHeader(LinearLayout.vertical().spacing(4));
        header.defaultCellSetting().alignHorizontallyCenter();
        header.addChild(new StringWidget(TITLE_TEXT, this.font));

        this.description = header.addChild(new MultiLineTextWidget(DESCRIPTION_TEXT, this.font).setCentered(true));

        LinearLayout upperContentButtons = header.addChild(LinearLayout.horizontal().spacing(8));
        upperContentButtons.addChild(Button.builder(TextsWithFallbacks.CONTACT_DEVELOPER_TEXT, _ -> ConfirmMailScreen.open(this, DEVELOPER_MAIL, true)).build());
        upperContentButtons.addChild(Button.builder(TextsWithFallbacks.OFFICIAL_PAGE_TEXT, _ -> ConfirmLinkScreen.confirmLinkNow(this, FStatsApi.OFFICIAL_PAGE_URL, true)).build());

        LinearLayout footer = this.layout.addToFooter(LinearLayout.vertical().spacing(4));
        LinearLayout footerButtons = footer.addChild(LinearLayout.horizontal().spacing(8));
        footerButtons.addChild(
            CycleButton.builder(value -> Component.literal(value.toString()).withStyle(switch (value) {
                case ALL -> ChatFormatting.GREEN;
                case WITHOUT_LOCATION -> ChatFormatting.YELLOW;
                case NOTHING -> ChatFormatting.RED;
            }), mode)
            .withValues(values())
            .create(this.width / 2 - 155, 100, 150, 20, TextsWithFallbacks.COLLECT_MODE_TEXT, (_, mode) -> {
                this.mode = mode;
                switch (mode) {
                    case ALL -> ConfigManager.enable();
                    case WITHOUT_LOCATION -> ConfigManager.enableWithoutLocation();
                    case NOTHING -> ConfigManager.disable();
                    default -> throw new IllegalStateException("Unexpected value: " + mode);
                }
            })
        );
        footerButtons.addChild(Button.builder(CommonComponents.GUI_TO_TITLE, _ -> this.onClose()).build());

        LinearLayout content = this.layout.addToContents(LinearLayout.vertical().spacing(8));
        this.fStatsWidget = content.addChild(new FStatsWidget(0, 0, this.width - 40, this.layout.getContentHeight(), this.font));
        this.fStatsWidget.setScrollConsumer(scroll -> this.savedScroll = scroll);
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }


    @Override
    protected void repositionElements() {
        if (this.fStatsWidget != null) {
            this.fStatsWidget.setScrollAmount(this.savedScroll);
            this.fStatsWidget.setWidth(this.width - 40);
            this.fStatsWidget.setHeight(this.layout.getContentHeight());
            this.fStatsWidget.updateLayout();
        }

        if (this.description != null) {
            this.description.setMaxWidth(this.width - 16);
        }

        this.layout.arrangeElements();
    }

    @Override
    protected void setInitialFocus() {
        if (this.fStatsWidget != null) {
            this.setInitialFocus(this.fStatsWidget);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}

