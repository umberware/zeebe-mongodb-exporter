package io.zeebe.exporter.mongo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.exporter.api.Exporter;
import io.camunda.zeebe.exporter.api.context.Context;
import io.camunda.zeebe.exporter.api.context.Controller;
import io.camunda.zeebe.protocol.record.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class MongoExporter implements Exporter {
    private Logger logger = LoggerFactory.getLogger(getClass().getPackageName());
    private final ObjectMapper exporterBuilder = new ObjectMapper();
    private Controller controller;
    private long lastPosition = -1;
    protected MongoExporterConfiguration exporterConfiguration;
    protected MongoExporterClient exporterClient;

    @Override
    public void configure(Context context) {
        this.logger = context.getLogger();
        this.logger.info("MongoExporter: Configuring the exporter...");
        this.exporterConfiguration = context.getConfiguration().instantiate(MongoExporterConfiguration.class);
        context.setFilter(new MongoExporterFilter(this.exporterConfiguration));
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
        if (this.lastPosition != actualPosition) {
            try {
                String recordAsJson = this.exporterBuilder.writeValueAsString(record.getValue());
                String collection = this.exporterConfiguration.getCollectionNameByEvent(record.getValueType());
                Map<String, Object> recordAsMap = this.exporterBuilder.readValue(recordAsJson, new TypeReference<Map<String, Object>>() {
                });
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
}
