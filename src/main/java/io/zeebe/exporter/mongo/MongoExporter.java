package io.zeebe.exporter.mongo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.exporter.api.Exporter;
import io.camunda.zeebe.exporter.api.context.Context;
import io.camunda.zeebe.exporter.api.context.Controller;
import io.camunda.zeebe.protocol.record.Record;

import java.util.Map;

public class MongoExporter implements Exporter {
    private final MongoExporterLogger logger = new MongoExporterLogger();
    private final ObjectMapper exporterBuilder = new ObjectMapper();
    protected Controller controller;
    private long lastPosition = -1;
    protected MongoExporterConfiguration exporterConfiguration;
    protected MongoExporterClient exporterClient;

    @Override
    public void configure(Context context) {
        this.logger.info("MongoExporter: Configuring the exporter...");
        this.exporterConfiguration = context.getConfiguration().instantiate(MongoExporterConfiguration.class);
        this.logger.setLoggerConfiguration(this.exporterConfiguration.getLogger());
        context.setFilter(new MongoExporterFilter(this.exporterConfiguration));
        this.logger.info("Logger configuration: " + this.exporterConfiguration.logger.toString());
        this.logger.info("Data allowed to be exported: " + this.exporterConfiguration.data.toString());
        this.logger.info("Event Data allowed to be exported: " + this.exporterConfiguration.dataType.toString());
        this.logger.info("MongoExporter: Configured.");
    }

    @Override
    public void open(Controller controller) {
        this.logger.info("MongoExporter: Opening exporter...");
        this.controller = controller;
        this.exporterConfiguration.initialize();
        this.exporterClient = new MongoExporterClient(this.exporterConfiguration);
        this.logger.info("MongoExporter: Is ready.");
    }

    @Override
    public void close() {
        this.logger.info("MongoExporter: Closing the exporter.");
        this.exporterConfiguration.mongoClient.close();
    }

    @Override
    public void export(Record<?> record) {
        long actualPosition = record.getPosition();
        this.logger.debug("Record position: " + record.getPosition() + "\nRecord: " + record.toString());
        if (this.canBeExported(record, actualPosition)) {
            try {
                String recordAsJson = this.exporterBuilder.writeValueAsString(record.getValue());
                String collection = this.exporterConfiguration.getCollectionNameByEvent(record.getValueType());
                Map<String, Object> recordAsMap = this.exporterBuilder.readValue(recordAsJson, new TypeReference<Map<String, Object>>() {});
                recordAsMap.put("intent", record.getIntent());
                recordAsMap.put("recordType", record.getRecordType());
                recordAsMap.put("valueType", record.getValueType());
                recordAsMap.put("timestamp", record.getTimestamp());
                recordAsMap.put("key", record.getKey());
                recordAsMap.put("position", record.getPosition());

                this.exporterClient.insertRecord(collection, this.exporterBuilder.writeValueAsString(recordAsMap));
                this.controller.updateLastExportedRecordPosition(actualPosition);
                this.lastPosition = actualPosition;
            } catch(JsonProcessingException e){
                this.logger.info("MongoExporter: Error when converting object: " + record.getValueType() + ":" + record.getRecordType());
                throw new RuntimeException(e);
            }
        } else {
            this.logger.info("Position: " + actualPosition + ", was already exported!");
        }
    }

    private boolean canBeExported(Record<?> record, long actualPosition) {
        return this.lastPosition != actualPosition &&
               this.exporterConfiguration.shouldExportRecord(record.getRecordType(), record.getValueType());
    }
}
