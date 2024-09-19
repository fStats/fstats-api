package dev.syoritohatsuki.fstatsapi.network;

import com.google.gson.GsonBuilder;
import dev.syoritohatsuki.fstatsapi.config.ConfigManager;
import dev.syoritohatsuki.fstatsapi.dto.Metrics;
import dev.syoritohatsuki.fstatsapi.logs.LogManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.server.dedicated.ServerPropertiesHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class Request {

    private static final String HIDDEN_LOCATION_CODE = "XXX";

    private static Map<Integer, String> getProjects() {
        return FabricLoader.getInstance().getAllMods().stream().filter(modContainer -> modContainer.getMetadata().getCustomValue("fstats") != null).collect(toMap(modContainer -> modContainer.getMetadata().getCustomValue("fstats").getAsNumber().intValue(), modContainer -> modContainer.getMetadata().getVersion().getFriendlyString()));
    }

    private static String getMinecraftVersion() {
        return SharedConstants.getGameVersion().getName();
    }

    private static Boolean getOnlineMode() {
        if (isServerSide()) return ServerPropertiesHandler.load(Paths.get("server.properties")).onlineMode;
        else return null;
    }

    private static char getOperatingSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) return 'w';
        else if (osName.contains("linux")) return 'l';
        else if (osName.contains("mac")) return 'm';
        else return 'o';
    }

    private static String getLocation() {

        if (ConfigManager.read().isLocationHide()) return HIDDEN_LOCATION_CODE;

        try {
            URL location = new URI("https://ip2c.org/self").toURL();
            String response = new BufferedReader(new InputStreamReader(location.openStream())).readLine();
            return response.split(";")[2];
        } catch (IOException | URISyntaxException e) {
            if (ConfigManager.read().getMessages().isWarningsEnabled()) {
                LogManager.logger.warn("Can't convert IP to location");
                LogManager.logger.warn(e);
            }
            return HIDDEN_LOCATION_CODE;
        }
    }

    static String getFabricApiVersion() {
        var fabricApi = FabricLoader.getInstance().getModContainer("fabric-api");
        return fabricApi.map(modContainer -> modContainer.getMetadata().getVersion().getFriendlyString()).orElse(null);
    }

    static boolean isServerSide() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    }

    public static String getJson() {
        return new GsonBuilder().create().toJson(new Metrics(getProjects(), new Metrics.Metric(getMinecraftVersion(), getOnlineMode(), getOperatingSystem(), getLocation(), getFabricApiVersion(), isServerSide())));
    }
}