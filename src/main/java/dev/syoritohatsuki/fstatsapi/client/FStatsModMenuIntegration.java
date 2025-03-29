package dev.syoritohatsuki.fstatsapi.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.syoritohatsuki.fstatsapi.client.gui.screen.FStatsScreen;

public class FStatsModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return FStatsScreen::new;
    }
}
