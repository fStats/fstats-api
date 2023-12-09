package dev.syoritohatsuki.fstatsapi;

import com.google.gson.Gson;
import dev.syoritohatsuki.fstatsapi.config.ConfigManager;
import dev.syoritohatsuki.fstatsapi.dto.Metrics;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FStatsApi {

    public static final String MOD_ID = "fstats-api";
    public static final Logger logger = LogManager.getLogger();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void sendMetricRequest(String version, boolean onlineMode) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextStepTime = now.truncatedTo(ChronoUnit.HOURS).plusMinutes(30);
        if (now.isAfter(nextStepTime)) nextStepTime = nextStepTime.plusMinutes(30);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                HttpClient.newHttpClient().send(HttpRequest.newBuilder().uri(URI.create("https://api.fstats.dev/v2/metrics")).header("Content-Type", "application/json").header("User-Agent", "fstats-api").POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(requestBody(version, onlineMode)))).build(), HttpResponse.BodyHandlers.ofString());
                if (ConfigManager.read().getMessages().isInfosEnabled()) {
                    logger.info("Metric data sent to https://fstats.dev");
                }
            } catch (Exception e) {
                if (ConfigManager.read().getMessages().isErrorsEnabled()) {
                    logger.error("Could not submit fStats metrics data: " + e.getLocalizedMessage());
                }
            }
        }, Duration.between(now, nextStepTime).toMillis(), TimeUnit.MILLISECONDS.convert(30, TimeUnit.MINUTES), TimeUnit.MILLISECONDS);
    }

    private static Metrics requestBody(String version, boolean onlineMode) {
        Map<Integer, String> projectIds = new HashMap<>();
        FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
            Integer fstatsValue = modContainer.getMetadata().getCustomValue("fstats") != null ? modContainer.getMetadata().getCustomValue("fstats").getAsNumber().intValue() : null;
            if (fstatsValue != null) {
                projectIds.put(fstatsValue, modContainer.getMetadata().getVersion().getFriendlyString());
            }
        });

        return new Metrics(projectIds, new Metrics.Metric(version, onlineMode, getOs(), getLocation(), getFabricApiVersion()));
    }

    private static String getFabricApiVersion() {
        return FabricLoader.getInstance().getModContainer("fabric-api").map(modContainer -> modContainer.getMetadata().getVersion().getFriendlyString()).orElse(null);
    }

    private static char getOs() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) return 'w';
        else if (osName.contains("linux")) return 'l';
        else if (osName.contains("mac")) return 'm';
        else return 'o';
    }

    private static String getLocation() {

        if (ConfigManager.read().isLocationHide()) return "unknown";

        try {
            URL ip = new URL("https://checkip.amazonaws.com/");
            URL location = new URL("https://ip2c.org/" + new BufferedReader(new InputStreamReader(ip.openStream())).readLine());
            String response = new BufferedReader(new InputStreamReader(location.openStream())).readLine();
            return response.split(";")[3];
        } catch (IOException e) {
            if (ConfigManager.read().getMessages().isWarningsEnabled()) {
                logger.warn("Can't convert IP to location: " + e.getLocalizedMessage());
            }
            return "unknown";
        }
    }
}
