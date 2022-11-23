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


object FStatsApi {

    private val scheduler = Executors.newScheduledThreadPool(1) { Thread(it, "fStats-Metrics") }

    private fun Metric.sendRequest() {
        Runnable {
            scheduler.execute {
                try {
                    HttpClient.newHttpClient().send(
                        HttpRequest.newBuilder()
                            .uri(URI.create("https://api.fstats.dev/v1/metrics"))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(Gson().toJson(this)))
                            .build(),
                        HttpResponse.BodyHandlers.ofString()
                    )
                    println("Next data sended to fStats: $this")
                } catch (e: Exception) {
                    println("Could not submit bStats metrics data: ${e.localizedMessage}")
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

    fun sendClientData(client: MinecraftClient, projectId: Int, modId: String) {
        Metric(
            projectId = projectId,
            isServer = false,
            minecraftVersion = client.game.version.id,
            modVersion = getModVersion(modId),
            os = getOs()
        ).sendRequest()
    }

    fun sendServerData(server: MinecraftServer, projectId: Int, modId: String) {
        Metric(
            projectId = projectId,
            isServer = true,
            minecraftVersion = server.version,
            isOnlineMode = server.isOnlineMode,
            modVersion = getModVersion(modId),
            os = getOs()
        ).sendRequest()
    }

    private fun getModVersion(modId: String): String =
        FabricLoader.getInstance().getModContainer(modId).get().metadata.version.friendlyString

    private fun getOs(): Char = when {
        System.getProperty("os.name").lowercase().contains("windows") -> 'w'
        System.getProperty("os.name").lowercase().contains("linux") -> 'l'
        System.getProperty("os.name").lowercase().contains("mac") -> 'm'
        else -> 'o'
    }
}