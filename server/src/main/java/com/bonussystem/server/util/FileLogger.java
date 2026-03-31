package com.bonussystem.server.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileLogger {

    private static final Logger logger = LogManager.getLogger(FileLogger.class);

    public static void log(String message) { logger.info(message); }

    public static void log(String message, Exception e) { logger.error(message, e); }
}