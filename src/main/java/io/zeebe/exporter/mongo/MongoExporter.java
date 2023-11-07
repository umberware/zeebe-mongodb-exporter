package io.zeebe.exporter.mongo;

import io.camunda.zeebe.exporter.api.Exporter;
import io.camunda.zeebe.exporter.api.context.Context;
import io.camunda.zeebe.exporter.api.context.Controller;
import io.camunda.zeebe.protocol.record.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoExporter implements Exporter {
    private Logger logger = LoggerFactory.getLogger(getClass().getPackageName());
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
        String collection = this.exporterConfiguration.getCollectionNameByEvent(record.getValueType());

        if (this.exporterConfiguration.shouldExportRecord(record.getRecordType(), record.getValueType()) && collection != null) {
            this.exporterClient.insertRecord(collection, record.getValue().toJson());
        } else {
            this.logger.info("MongoExporter: Will not be exported: " + record.getValueType() + ":" + record.getRecordType());
        }
    }
}
