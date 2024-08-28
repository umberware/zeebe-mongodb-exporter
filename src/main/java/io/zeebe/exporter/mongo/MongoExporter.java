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
                String collection = this.exporterConfiguration.getCollectionNameByEvent(record.getValueType(), record.getValue());
                if (this.isRecordTimestampAllowedToBeExported(record) || this.isProcessDefinition(collection)) {
                    String recordAsJson = this.exporterBuilder.writeValueAsString(record.getValue());
                    Map<String, Object> recordAsMap = this.exporterBuilder.readValue(recordAsJson, new TypeReference<>() {
                    });
                    recordAsMap.put("intent", record.getIntent());
                    recordAsMap.put("recordType", record.getRecordType());
                    recordAsMap.put("valueType", record.getValueType());
                    recordAsMap.put("timestamp", record.getTimestamp());
                    recordAsMap.put("key", record.getKey());
                    recordAsMap.put("position", record.getPosition());

                    this.exporterClient.updateOrInsert(collection, this.exporterBuilder.writeValueAsString(recordAsMap));
                    this.logger.debug("Exporting: " + actualPosition + ":" + record.getTimestamp() + " to collection: " + collection);
                } else {
                    this.logger.debug("Position: " + actualPosition + ":" + record.getTimestamp() + ", is before of " + this.exporterConfiguration.data.fromTimestamp + "!");
                }
                this.controller.updateLastExportedRecordPosition(actualPosition);
                this.lastPosition = actualPosition;
            } catch(JsonProcessingException e){
                this.logger.info("MongoExporter: Error when converting object: " + record.getValueType() + ":" + record.getRecordType());
                throw new RuntimeException(e);
            }
        } else {
            this.logger.info("Position: " + actualPosition + ":" + record.getTimestamp() + ", will not be exported!");
        }
    }

    private boolean canBeExported(Record<?> record, long actualPosition) {
        return this.lastPosition != actualPosition &&
            this.exporterConfiguration.shouldExportRecord(record.getRecordType(), record.getValueType());
    }
    private boolean isRecordTimestampAllowedToBeExported(Record<?> record) {
        return record.getTimestamp() >= this.exporterConfiguration.data.fromTimestamp;
    }
    private boolean isProcessDefinition(String collection) {
        return collection.equals("process") || collection.equals("deployment");
    }
}
