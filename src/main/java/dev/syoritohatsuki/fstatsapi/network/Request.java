package dev.syoritohatsuki.fstatsapi.network;

import com.google.gson.GsonBuilder;
import dev.syoritohatsuki.fstatsapi.config.ConfigManager;
import dev.syoritohatsuki.fstatsapi.dto.Metrics;
import dev.syoritohatsuki.fstatsapi.logs.LogManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.server.dedicated.ServerPropertiesHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class Request {

    private static Map<Integer, String> getProjects() {
        return FabricLoader.getInstance().getAllMods().stream().filter(modContainer -> modContainer.getMetadata().getCustomValue("fstats") != null).collect(toMap(modContainer -> modContainer.getMetadata().getCustomValue("fstats").getAsNumber().intValue(), modContainer -> modContainer.getMetadata().getVersion().getFriendlyString()));
    }

    private static String getMinecraftVersion() {
        return SharedConstants.getGameVersion().getName();
    }

    private static boolean getOnlineMode() {
        return ServerPropertiesHandler.load(Paths.get("server.properties")).onlineMode;
    }

    private static char getOperatingSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) return 'w';
        else if (osName.contains("linux")) return 'l';
        else if (osName.contains("mac")) return 'm';
        else return 'o';
    }

    private static String getLocation() {

        if (ConfigManager.read().isLocationHide()) return "unknown";

        try {
            URL ip = new URL("https://checkip.amazonaws.com/");
            URL location = new URL("https://ip2c.org/" + new BufferedReader(new InputStreamReader(ip.openStream())).readLine());
            String response = new BufferedReader(new InputStreamReader(location.openStream())).readLine();
            return response.split(";")[3];
        } catch (IOException e) {
            if (ConfigManager.read().getMessages().isWarningsEnabled()) {
                LogManager.logger.warn("Can't convert IP to location");
                LogManager.logger.warn(e);
            }
            return "unknown";
        }
    }

    static String getFabricApiVersion() {
        var fabricApi = FabricLoader.getInstance().getModContainer("fabric-api");
        return fabricApi.map(modContainer -> modContainer.getMetadata().getVersion().getFriendlyString()).orElse(null);
    }

    public static String getJson() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(new Metrics(getProjects(), new Metrics.Metric(getMinecraftVersion(), getOnlineMode(), getOperatingSystem(), getLocation(), getFabricApiVersion())));
    }
}