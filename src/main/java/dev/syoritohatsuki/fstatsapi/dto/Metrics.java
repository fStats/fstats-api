package dev.syoritohatsuki.fstatsapi.dto;

import java.util.Map;

public record Metrics(Map<Integer, String> projectIds, Metric metric) {
    public record Metric(String minecraftVersion, boolean isOnlineMode, char os, String location,
                         String fabricApiVersion) {
    }
}