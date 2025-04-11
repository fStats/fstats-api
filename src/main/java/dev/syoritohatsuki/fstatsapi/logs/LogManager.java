package dev.syoritohatsuki.fstatsapi.logs;

import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.apache.logging.log4j.LogManager.getLogger;

public class LogManager {

    private static final File logFile = Paths.get("fstats.log").toFile();
    public static final Logger logger = getLogger();
    private static final long now = System.currentTimeMillis();

    public static void init() {
        if (logFile.exists()) return;

        try {
            if (!logFile.createNewFile()) logger.warn("Can't create log file");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLatestLog() {
        if (!logFile.canRead()) return null;

        try {
            var lines = Files.readAllLines(logFile.toPath());
            if (lines.isEmpty()) return null;
            return lines.getLast();
        } catch (IOException e) {
            logger.warn("Can't get latest log");
            logger.warn(e);
            return null;
        }
    }

    public static void writeLog(String message) {
        if (!logFile.canWrite()) {
            logger.warn("Can't access to log file");
            return;
        }

        try (FileWriter fileWriter = new FileWriter(logFile, true)) {
            fileWriter.write(now + "," + message + System.lineSeparator());
        } catch (IOException e) {
            logger.warn("Can't write log to file");
            logger.warn(e);
        }
    }
}
