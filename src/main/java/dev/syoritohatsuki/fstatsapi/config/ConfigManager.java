package dev.syoritohatsuki.fstatsapi.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.syoritohatsuki.fstatsapi.logs.LogManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static dev.syoritohatsuki.fstatsapi.FStatsApi.MOD_ID;

public final class ConfigManager {

    private static final File configDir = Paths.get("", "config", MOD_ID).toFile();
    private static final File configFile = new File(configDir, "config.json");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final Config defaultConfig = new Config(2, Config.Mode.ALL, new Config.Messages(true, true, true));

    public static void init() {
        if (!configDir.exists() && !configDir.mkdirs()) {
            LogManager.logger.warn("Can't create config dirs");
        }

        if (!configFile.exists()) {
            try {
                if (!configFile.createNewFile()) {
                    LogManager.logger.warn("Can't create config file");
                    return;
                }
                Files.writeString(configFile.toPath(), gson.toJson(defaultConfig));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (!Objects.equals(read().getVersion(), defaultConfig.getVersion())) {
            try {
                LogManager.logger.warn("Looks like config is deprecated... Updating...");

                var mode = read().getMode() == null ? defaultConfig.getMode() : read().getMode();
                var messages = read().getMessages() == null ? defaultConfig.getMessages() : read().getMessages();

                Files.writeString(configFile.toPath(), gson.toJson(new Config(defaultConfig.getVersion(), mode, messages)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Config read() {
        try {
            return gson.fromJson(Files.readString(configFile.toPath()), Config.class);
        } catch (Exception e) {
            LogManager.logger.error("Can't read config or it don't exist");
            try {
                if (configFile.exists()) {
                    LogManager.logger.info("Backup existing config...");
                    Files.copy(configFile.toPath(), new File(configDir, "backup_config_" + System.currentTimeMillis() +".json").toPath());
                }
                init();
                return gson.fromJson(Files.readString(configFile.toPath()), Config.class);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
