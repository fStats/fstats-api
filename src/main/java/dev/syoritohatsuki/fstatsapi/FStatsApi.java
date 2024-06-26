package dev.syoritohatsuki.fstatsapi;

import dev.syoritohatsuki.fstatsapi.config.ConfigManager;
import dev.syoritohatsuki.fstatsapi.logs.LogManager;
import dev.syoritohatsuki.fstatsapi.network.Request;
import net.fabricmc.loader.api.FabricLoader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class FStatsApi {

    public static final String MOD_ID = "fstats-api";
    private static final String USER_AGENT = "fstats/fstats-api/" + getFStatsVersion() + " (fstats.dev)";

    private static final int requestSendDelay = 1000 * 60 * 30;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public static void sendMetricRequest() {

        long now = System.currentTimeMillis();
        long nextStepTime = now;

        var log = LogManager.getLatestLog();
        long lastRequest = (log != null) ? Long.parseLong(log.split(",")[0]) : now;

        long diff = now - lastRequest;
        if (diff > 0 && diff < requestSendDelay) {
            nextStepTime = nextStepTime + TimeUnit.MINUTES.toMillis(ThreadLocalRandom.current().nextInt(30, 41)) - diff;
        }

        LogManager.logger.warn(Request.getJson());

        scheduler.scheduleAtFixedRate(() -> {
            try {
                var url = URI.create("https://api.fstats.dev/v2/metrics");
                var postBody = HttpRequest.BodyPublishers.ofString(Request.getJson());

                HttpClient.newHttpClient().send(HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json").header("User-Agent", USER_AGENT).POST(postBody).build(), HttpResponse.BodyHandlers.ofString());

                var message = "Metric data sent to https://fstats.dev";
                if (ConfigManager.read().getMessages().isInfosEnabled()) {
                    LogManager.logger.info(message);
                }
                LogManager.writeLog(message);
            } catch (Exception e) {
                if (ConfigManager.read().getMessages().isErrorsEnabled()) {
                    LogManager.logger.error("Could not submit fStats metrics data");
                    LogManager.logger.error(e);
                }
            }
        }, nextStepTime - now, requestSendDelay, TimeUnit.MILLISECONDS);
    }

    private static String getFStatsVersion() {
        return FabricLoader.getInstance().getModContainer("fstats-api").map(modContainer -> modContainer.getMetadata().getVersion().getFriendlyString()).orElse(null);
    }
}
