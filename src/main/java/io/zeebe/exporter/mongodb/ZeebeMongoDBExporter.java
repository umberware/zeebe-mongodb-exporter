package io.zeebe.exporter.mongodb;

import io.camunda.zeebe.exporter.api.Exporter;
import io.camunda.zeebe.exporter.api.context.Context;
import io.camunda.zeebe.exporter.api.context.Controller;
import io.camunda.zeebe.protocol.record.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZeebeMongoDBExporter implements Exporter {
    private Logger logger = LoggerFactory.getLogger(getClass().getPackageName());

    @Override
    public void configure(Context context) throws Exception {
        this.logger = context.getLogger();
        this.logger.info("Configuring ZeebeMongoDBExporter...");
        this.logger.info("Configured ZeebeMongoDBExporter.");
    }

    @Override
    public void open(Controller controller) {
        this.logger.info("Opening ZeebeMongoDBExporter...");
        this.logger.info("ZeebeMongoDBExporter is ready.");
    }

    @Override
    public void close() {
        this.logger.info("ZeebeMongoDBExporter is closed.");
    }

    @Override
    public void export(Record<?> record) {
        this.logger.info("record = " + record);
    }
}
