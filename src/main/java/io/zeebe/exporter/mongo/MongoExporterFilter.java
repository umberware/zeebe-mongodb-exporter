package io.zeebe.exporter.mongo;

import io.camunda.zeebe.exporter.api.context.Context;
import io.camunda.zeebe.protocol.record.RecordType;
import io.camunda.zeebe.protocol.record.ValueType;

public class MongoExporterFilter implements Context.RecordFilter {

    private final MongoExporterConfiguration configuration;

    public MongoExporterFilter(final MongoExporterConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean acceptType(final RecordType recordType) {
        return configuration.shouldExportEventType(recordType);
    }

    @Override
    public boolean acceptValue(final ValueType valueType) {
        return configuration.shouldExportRecordType(valueType);
    }
}
