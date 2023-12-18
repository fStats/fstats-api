package dev.syoritohatsuki.fstatsapi.logs;

import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.apache.logging.log4j.LogManager.getLogger;

public class LogManager {

    private enum Level {
        ERROR, INFO, WARN
    }

    private static final File logFile = Paths.get("fstats.log").toFile();

    private static final Logger logger = getLogger();
    private static final long now = System.currentTimeMillis();

    public static void init() {
        if (logFile.exists()) return;

        try {
            if (!logFile.createNewFile()) logger.warn("Can't create log file");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Logger getLogFreeLogger() {
        return logger;
    }

    public static void info(String message) {
        logger.info(message);
        writeLog(Level.INFO, message);
    }

    public static void warn(String message) {
        logger.warn(message);
        writeLog(Level.WARN, message);
    }

    public static void error(String message) {
        logger.error(message);
        writeLog(Level.ERROR, message);
    }

    public static String getLatestLog() {
        if (logFile.canRead()) {
            try {
                var lines = Files.readAllLines(logFile.toPath());
                if (lines.isEmpty()) return null;
                return lines.get(lines.size() - 1);
            } catch (IOException e) {
                logger.warn("Can't get latest log");
                logger.warn(e);
            }
        }
        return null;
    }

    private static void writeLog(Level level, String message) {
        if (!logFile.canWrite()) {
            logger.warn("Can't access to log file");
            return;
        }

        try (FileWriter fileWriter = new FileWriter(logFile, true)) {
            fileWriter.write(now + "," + level + "," + message + System.lineSeparator());
        } catch (IOException e) {
            logger.warn("Can't write log to file");
            logger.warn(e);
        }
    }
}