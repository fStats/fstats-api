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

object FStatsApi {

    private val httpClient: HttpClient = HttpClient.newHttpClient()

    fun sendClientData(client: MinecraftClient, projectId: Int, modId: String) {
        httpClient.send(
            HttpRequest.newBuilder()
                .uri(URI.create("https://api.fstats.dev/v1/metrics"))
                .header("Content-Type", "application/json")
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        clientRequestJson(
                            Metric(
                                projectId = projectId,
                                isServer = false,
                                minecraftVersion = client.game.version.id,
                                modVersion = getModVersion(modId),
                                os = getOs()
                            )
                        )
                    )
                )
                .build(), HttpResponse.BodyHandlers.ofString()
        )
    }

    fun sendServerData(server: MinecraftServer, projectId: Int, modId: String) {
        httpClient.send(
            HttpRequest.newBuilder()
                .uri(URI.create("https://api.fstats.dev/v1/metrics"))
                .header("Content-Type", "application/json")
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        serverRequestJson(
                            Metric(
                                projectId = projectId,
                                isServer = true,
                                minecraftVersion = server.version,
                                isOnlineMode = server.isOnlineMode,
                                modVersion = getModVersion(modId),
                                os = getOs()
                            )
                        )
                    )
                )
                .build(), HttpResponse.BodyHandlers.ofString()
        )
    }

    private fun getModVersion(modId: String): String {
        return FabricLoader.getInstance().getModContainer(modId).get().metadata.version.friendlyString
    }

    private fun getOs(): Char {
        return when {
            System.getProperty("os.name").lowercase().contains("windows") -> 'w'
            System.getProperty("os.name").lowercase().contains("linux") -> 'l'
            System.getProperty("os.name").lowercase().contains("mac") -> 'm'
            else -> 'o'
        }
    }

    private fun clientRequestJson(metric: Metric): String {
        return Gson().toJson(metric)
    }

    private fun serverRequestJson(metric: Metric): String {
        return Gson().toJson(metric)
    }
}