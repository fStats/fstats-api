package dev.syoritohatsuki.fstatsapi

import com.google.gson.Gson
import dev.syoritohatsuki.fstatsapi.dto.Metric
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.server.MinecraftServer
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class FStatsApi(private val projectId: Int, private val modId: String) {

    private val scheduler = Executors.newScheduledThreadPool(1) { Thread(it, "fStats-Metrics") }

    private fun sendMetricRequest(metric: Metric) {
        Runnable {
            scheduler.execute {
                try {
                    HttpClient.newHttpClient().send(
                        HttpRequest.newBuilder()
                            .uri(URI.create("https://api.fstats.dev/v1/metrics"))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(Gson().toJson(metric)))
                            .build(),
                        HttpResponse.BodyHandlers.ofString()
                    )
                    println("Metric data sent to https://fstats.dev by ${getModName()}")
                } catch (e: Exception) {
                    println("Could not submit fStats metrics data: ${e.localizedMessage}")
                }
            }
        }.also {
            (1000 * 60 * (3 + Math.random() * 3)).toLong().apply {
                scheduler.schedule(it, this, TimeUnit.MILLISECONDS)
                scheduler.scheduleAtFixedRate(
                    it, this + (1000 * 60 * (Math.random() * 30)).toLong(),
                    (1000 * 60 * 30).toLong(), TimeUnit.MILLISECONDS
                )
            }
        }

    }

    fun sendClientData(client: MinecraftClient) {
        sendMetricRequest(
            Metric(
                projectId = projectId,
                isServer = false,
                minecraftVersion = client.game.version.id,
                modVersion = getModVersion(),
                os = getOs()
            )
        )
    }

    fun sendServerData(server: MinecraftServer) {
        if (server.isDedicated) {
            sendMetricRequest(
                Metric(
                    projectId = projectId,
                    isServer = true,
                    minecraftVersion = server.version,
                    isOnlineMode = server.isOnlineMode,
                    modVersion = getModVersion(),
                    os = getOs()
                )
            )
        }
    }

    fun sendExceptionData(e: Exception) {
        try {
            HttpClient.newHttpClient().send(
                HttpRequest.newBuilder()
                    .uri(URI.create("https://api.fstats.dev/v1/exceptions"))
                    .header("Content-Type", "application/json")
                    .POST(
                        HttpRequest.BodyPublishers.ofString(
                            Gson().toJson(
                                dev.syoritohatsuki.fstatsapi.dto.Exception(
                                    projectId,
                                    e.stackTrace.joinToString("\n")
                                )
                            )
                        )
                    )
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            )
            println("Exception sent to https://fstats.dev by ${getModName()}")
        } catch (e: Exception) {
            println("Could not submit fStats exception data: ${e.localizedMessage}")
        }
    }

    private fun getModName(): String =
        FabricLoader.getInstance().getModContainer(modId).get().metadata.name

    private fun getModVersion(): String =
        FabricLoader.getInstance().getModContainer(modId).get().metadata.version.friendlyString

    private fun getOs(): Char = when {
        System.getProperty("os.name").contains("windows", true) -> 'w'
        System.getProperty("os.name").contains("linux", true) -> 'l'
        System.getProperty("os.name").contains("mac", true) -> 'm'
        else -> 'o'
    }
}