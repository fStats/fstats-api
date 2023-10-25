package dev.syoritohatsuki.fstatsapi;

import com.google.gson.Gson;
import dev.syoritohatsuki.fstatsapi.dto.Metrics;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FStatsApi {

    private static final Logger logger = LoggerFactory.getLogger(FStatsApi.class);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void sendMetricRequest(String version, boolean onlineMode) {
        Runnable runnable = () -> scheduler.execute(() -> {
            try {
                HttpClient.newHttpClient().send(HttpRequest.newBuilder().uri(URI.create("https://api.fstats.dev/v2/metrics")).header("Content-Type", "application/json").header("User-Agent", "fstats-api").POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(requestBody(version, onlineMode)))).build(), HttpResponse.BodyHandlers.ofString());
                logger.info("Metric data sent to https://fstats.dev");
            } catch (Exception e) {
                logger.error("Could not submit fStats metrics data: " + e.getLocalizedMessage());
            }
        });

        long initialDelay = (long) (1000 * 60 * (3 + Math.random() * 3));
        long periodicDelay = (long) (1000 * 60 * (Math.random() * 30));
        long fixedRate = (1000 * 60 * 30);

        scheduler.schedule(runnable, initialDelay, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(runnable, initialDelay + periodicDelay, fixedRate, TimeUnit.MILLISECONDS);
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
        if (osName.contains("windows")) {
            return 'w';
        } else if (osName.contains("linux")) {
            return 'l';
        } else if (osName.contains("mac")) {
            return 'm';
        } else {
            return 'o';
        }
    }

    private static String getLocation() {
        try {
            URL ip = new URL("https://checkip.amazonaws.com/");
            URL location = new URL("https://ip2c.org/" + new BufferedReader(new InputStreamReader(ip.openStream())).readLine());
            String response = new BufferedReader(new InputStreamReader(location.openStream())).readLine();
            return response.split(";")[3];
        } catch (IOException e) {
            logger.warn("Can't convert IP to location: " + e.getLocalizedMessage());
            return "unknown";
        }
    }
}
