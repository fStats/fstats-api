package dev.syoritohatsuki.fstatsapi;

import dev.syoritohatsuki.fstatsapi.config.ConfigManager;
import dev.syoritohatsuki.fstatsapi.logs.LogManager;
import dev.syoritohatsuki.fstatsapi.network.Request;
import net.fabricmc.loader.api.FabricLoader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.*;

public class FStatsApi {

    public static final String MOD_ID = "fstats-api";
    private static final String USER_AGENT = "fstats/fstats-api/" + getFStatsVersion() + " (fstats.dev)";

    public static final String API_URL = "https://api.fstats.dev/";
    public static final String OFFICIAL_PAGE_URL = "https://fstats.dev/";

    private static final int requestSendDelay = 1000 * 60 * 30;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static ScheduledFuture<?> requestSendingTaskFuture = null;

    public static ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public static ScheduledFuture<?> getRequestSendingTaskFuture() {
        return requestSendingTaskFuture;
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

        requestSendingTaskFuture = scheduler.scheduleAtFixedRate(() -> {
            try (var client = HttpClient.newHttpClient()) {
                var url = URI.create(API_URL + "v3/metrics");
                var postBody = HttpRequest.BodyPublishers.ofString(Request.getJson());


                var response = client.send(HttpRequest.newBuilder().uri(url).header("Content-Type", "application/json").header("User-Agent", USER_AGENT).POST(postBody).build(), HttpResponse.BodyHandlers.ofString());

                if (!response.body().contains("201")) {
                    throw new RuntimeException("Error while sending request: " + response.body());
                }

                var message = "Metric data sent to " + OFFICIAL_PAGE_URL;

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
        return FabricLoader.getInstance().getModContainer(MOD_ID).map(modContainer -> modContainer.getMetadata().getVersion().getFriendlyString()).orElse(null);
    }
}
