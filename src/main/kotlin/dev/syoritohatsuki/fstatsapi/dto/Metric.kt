package dev.syoritohatsuki.fstatsapi.dto

data class Metric(
    val minecraftVersion: String,
    val isOnlineMode: Boolean,
    val os: Char,
    val location: String,
    val fabricApiVersion: String
)

data class Metrics(
    val projectIds: Map<Int, String>,
    val metric: Metric
)