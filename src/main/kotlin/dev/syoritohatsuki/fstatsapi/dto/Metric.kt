package dev.syoritohatsuki.fstatsapi.dto

data class Metric(
    val projectId: Int,
    val isServer: Boolean,
    val minecraftVersion: String,
    val isOnlineMode: Boolean? = null,
    val modVersion: String,
    val os: Char
)
