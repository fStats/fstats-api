package dev.syoritohatsuki.fstatsapi.dto

data class Metric(
    val projectId: Int,
    val isServer: Boolean,
    val minecraftVersion: String,
    val isOnlineMode: Boolean? = null,
    val modVersion: String,
    val os: Char
) {
    override fun toString(): String = "Server: $isServer\n" +
            "Minecraft Version: $minecraftVersion\n" +
            "OnlineMode: $isOnlineMode\n" +
            "Mod Version: $modVersion\n" +
            "OS: $os"
}
