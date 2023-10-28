package dev.syoritohatsuki.fstatsapi.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static dev.syoritohatsuki.fstatsapi.FStatsApi.MOD_ID;
import static dev.syoritohatsuki.fstatsapi.FStatsApi.logger;

public final class ConfigManager {

    private static final File configDir = Paths.get("", "config", MOD_ID).toFile();

    private static final File configFile = new File(configDir, "config.json");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ConfigManager() {
        if (!configDir.exists()) configDir.mkdirs();
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                Files.writeString(configFile.toPath(), gson.toJson(new Config(true, false)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Config read() {
        try {
            return gson.fromJson(Files.readString(configFile.toPath()), Config.class);
        } catch (IOException e) {
            logger.error("Can't read config");
            try {
                logger.info("Backup config...");
                Files.copy(configFile.toPath(), new File(configDir, "backup_config.json").toPath());
                Files.writeString(configFile.toPath(), gson.toJson(new Config(true, false)));
                return gson.fromJson(Files.readString(configFile.toPath()), Config.class);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public record Config(Boolean enabled, Boolean hideLocation) {

    }
}