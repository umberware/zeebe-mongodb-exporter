package io.zeebe.exporter.mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoExporterLogger {
    private final Logger logger = LoggerFactory.getLogger(getClass().getPackageName());
    private MongoExporterLoggerLevel loggerLevel = MongoExporterLoggerLevel.INFO;

    public void debug(String message) {
        if (loggerLevel.level > 0) {
            this.logger.info(message);
        }
    }

    public void info(String message) {
        this.logger.info(message);
    }

    public void setLoggerConfiguration(MongoExporterConfiguration.LoggerConfiguration loggerConfiguration) {
        this.loggerLevel = loggerConfiguration.level;
    }
}
