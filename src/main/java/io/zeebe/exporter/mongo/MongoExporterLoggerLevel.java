package io.zeebe.exporter.mongo;

public enum MongoExporterLoggerLevel {
    INFO(0),
    DEBUG(1);
    public final int level;
    MongoExporterLoggerLevel(int level) {
        this.level = level;
    }
}
