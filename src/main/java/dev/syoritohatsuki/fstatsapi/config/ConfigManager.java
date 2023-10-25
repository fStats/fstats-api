package dev.syoritohatsuki.fstatsapi.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static dev.syoritohatsuki.fstatsapi.FStatsApi.MOD_ID;

final class UpgradesConfig {

    private final File configDir = Paths.get("", "config", MOD_ID).toFile();

    private final File configFile = new File(configDir, "upgrades.json");

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public UpgradesConfig() {
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

    public Config read() {
        try {
            return gson.fromJson(Files.readString(configFile.toPath()), Config.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public record Config(Boolean enabled, Boolean hideLocation) {

    }
}