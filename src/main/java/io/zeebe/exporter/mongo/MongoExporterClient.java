package io.zeebe.exporter.mongo;

import org.bson.Document;

public class MongoExporterClient {
    MongoExporterConfiguration mongoDBConfiguration;

    public MongoExporterClient(MongoExporterConfiguration mongoDBConfiguration) {
        this.mongoDBConfiguration = mongoDBConfiguration;
    }
    public void

    insertRecord(String collection, String record) {
        this.mongoDBConfiguration.mongoDatabase.getCollection(collection).insertOne(Document.parse(record));
    }
}
