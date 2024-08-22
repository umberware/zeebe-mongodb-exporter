package io.zeebe.exporter.mongo;

public enum MongoExporterLoggerLevel {
    NONE(0),
    INFO(1),
    DEBUG(2);
    public final int level;
    MongoExporterLoggerLevel(int level) {
        this.level = level;
    }
}
