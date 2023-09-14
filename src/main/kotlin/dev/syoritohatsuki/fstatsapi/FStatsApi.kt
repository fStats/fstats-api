package dev.syoritohatsuki.fstatsapi

import com.google.gson.Gson
import dev.syoritohatsuki.fstatsapi.dto.Metric
import dev.syoritohatsuki.fstatsapi.dto.Metrics
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


object FStatsApi {

    private val logger: Logger = LoggerFactory.getLogger(FStatsApi::class.java)

    private val scheduler = Executors.newScheduledThreadPool(1) {
        Thread(it, "fStats-Metrics")
    }

    fun sendMetricRequest(version: String, onlineMode: Boolean) {
        val runnable = Runnable {
            scheduler.execute {
                try {
                    HttpClient.newHttpClient().send(
                        HttpRequest.newBuilder().uri(URI.create("https://api.fstats.dev/v2/metrics"))
                            .header("Content-Type", "application/json").header("User-Agent", "fstats-api")
                            .POST(HttpRequest.BodyPublishers.ofString(Gson().toJson(requestBody(version, onlineMode))))
                            .build(), HttpResponse.BodyHandlers.ofString()
                    )

                    logger.info("Metric data sent to https://fstats.dev")
                } catch (e: Exception) {
                    logger.error("Could not submit fStats metrics data: ${e.localizedMessage}")
                }
            }
        }
        (1000 * 60 * (3 + Math.random() * 3)).toLong().apply {
            scheduler.schedule(runnable, this, TimeUnit.MILLISECONDS)
            scheduler.scheduleAtFixedRate(
                runnable,
                this + (1000 * 60 * (Math.random() * 30)).toLong(),
                (1000 * 60 * 30).toLong(),
                TimeUnit.MILLISECONDS
            )
        }
    }

    private fun requestBody(version: String, onlineMode: Boolean): Metrics = Metrics(
        mutableMapOf<Int, String>().apply {
            FabricLoader.getInstance().allMods.forEach {
                put(
                    it.metadata.customValues["fstats"]?.asNumber?.toInt() ?: return@forEach,
                    it.metadata.version.friendlyString
                )
            }
        }, Metric(
            version,
            onlineMode,
            getOs(),
            getLocation(),
            (FabricLoader.getInstance().getModContainer("fabric-api").get().metadata.version.friendlyString)
                ?: "unknown"
        )
    )

    private fun getOs(): Char {
        val osName = System.getProperty("os.name").lowercase()
        return when {
            osName.contains("windows") -> 'w'
            osName.contains("linux") -> 'l'
            osName.contains("mac") -> 'm'
            else -> 'o'
        }
    }

    private fun getLocation(): String {
        try {
            BufferedReader(InputStreamReader(URL("https://checkip.amazonaws.com/").openStream())).use { ip ->
                BufferedReader(InputStreamReader(URL("https://ip2c.org/" + ip.readLine()).openStream())).use { location ->
                    return location.readLine().split(';')[3]
                }
            }
        } catch (e: Exception) {
            logger.warn("Can't convert IP to location: " + e.localizedMessage)
            return "unknown"
        }
    }
}