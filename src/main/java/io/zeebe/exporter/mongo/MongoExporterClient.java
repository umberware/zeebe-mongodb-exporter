package io.zeebe.exporter.mongo;

import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

public class MongoExporterClient {
    MongoExporterConfiguration mongoDBConfiguration;

    public MongoExporterClient(MongoExporterConfiguration mongoDBConfiguration) {
        this.mongoDBConfiguration = mongoDBConfiguration;
    }
    public void

    updateOrInsert(String collection, String record) {
        Document parsedRecord = (Document.parse(record));
        Document event = new Document();

        event.append("timestamp", parsedRecord.get("timestamp"));
        event.append("intent", parsedRecord.get("intent"));
        event.append("position", parsedRecord.get("position"));

        parsedRecord.append("lastEvent", event);

        parsedRecord.remove("timestamp");
        parsedRecord.remove("intent");
        parsedRecord.remove("position");

        Document filter = new Document("key", parsedRecord.get("key"));
        Document updateOperation = new Document("$set", parsedRecord);
        UpdateOptions options = new UpdateOptions().upsert(true);

        updateOperation.append("$push", new Document("events", event));

        this.mongoDBConfiguration.mongoDatabase.getCollection(collection).updateOne(filter, updateOperation, options);
    }
}
