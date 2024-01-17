package io.zeebe.exporter.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.camunda.zeebe.protocol.record.RecordType;
import io.camunda.zeebe.protocol.record.ValueType;

public class MongoExporterConfiguration {
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;

    public ConnectionConfiguration connection = new ConnectionConfiguration();
    public DataTypeConfiguration dataType = new DataTypeConfiguration();
    public DataConfiguration data = new DataConfiguration();

    public LoggerConfiguration loggerConfiguration = new LoggerConfiguration();

    public void initialize() {
        this.mongoClient = MongoClients.create(connection.uri);
        this.mongoDatabase = this.mongoClient.getDatabase(connection.database);
    }

    public boolean shouldExportRecord(RecordType recordType, ValueType valueType) {
        return this.shouldExportRecordType(recordType)
                && this.shouldExportEventType(valueType);
    }

    public boolean shouldExportEventType(final ValueType valueType) {
        return switch (valueType) {
            case DEPLOYMENT -> data.deployment;
            case PROCESS -> data.process;
            case ERROR -> data.error;
            case INCIDENT -> data.incident;
            case JOB -> data.job;
            case JOB_BATCH -> data.jobBatch;
            case MESSAGE -> data.message;
            case MESSAGE_BATCH -> data.messageBatch;
            case MESSAGE_SUBSCRIPTION -> data.messageSubscription;
            case VARIABLE -> data.variable;
            case VARIABLE_DOCUMENT -> data.variableDocument;
            case PROCESS_INSTANCE -> data.processInstance;
            case PROCESS_INSTANCE_BATCH -> data.processInstanceBatch;
            case PROCESS_INSTANCE_CREATION -> data.processInstanceCreation;
            case PROCESS_INSTANCE_MODIFICATION -> data.processInstanceModification;
            case PROCESS_MESSAGE_SUBSCRIPTION -> data.processMessageSubscription;
            case DECISION_REQUIREMENTS -> data.decisionRequirements;
            case DECISION -> data.decision;
            case DECISION_EVALUATION -> data.decisionEvaluation;
            case CHECKPOINT -> data.checkpoint;
            case TIMER -> data.timer;
            case MESSAGE_START_EVENT_SUBSCRIPTION -> data.messageStartEventSubscription;
            case PROCESS_EVENT -> data.processEvent;
            case DEPLOYMENT_DISTRIBUTION -> data.deploymentDistribution;
            case ESCALATION -> data.escalation;
            case SIGNAL -> data.signal;
            case SIGNAL_SUBSCRIPTION -> data.signalSubscription;
            case RESOURCE_DELETION -> data.resourceDeletion;
            case COMMAND_DISTRIBUTION -> data.commandDistribution;
            case FORM -> data.form;
            default -> false;
        };
    }

    public boolean shouldExportRecordType(final RecordType recordType) {
        return switch (recordType) {
            case EVENT -> dataType.event;
            case COMMAND -> dataType.command;
            case COMMAND_REJECTION -> dataType.rejection;
            default -> false;
        };
    }

    public boolean isDebugAllowed() {
        return this.loggerConfiguration.debug;
    }

    public String getCollectionNameByEvent(ValueType valueType) {
        return valueType.toString().replaceAll("_", "-").toLowerCase();
    }

    public static class ConnectionConfiguration {
        public String uri = "localhost:27017";
        public String database = "zeebe";

        @Override
        public String toString() {
            return "MongoDBConnectionConfiguration{"
                    + "uri="
                    + uri
                    + ", database="
                    + database
                    + "}";
        }

    }

    public static class LoggerConfiguration {
        public boolean debug = false;
        @Override
        public String toString() {
            return "LoggerConfiguration{"
                    + "debug="
                    + debug
                    + "}";
        }
    }

    public static class DataTypeConfiguration {
        public boolean command = false;
        public boolean event = true;
        public boolean rejection = false;
        @Override
        public String toString() {
            return "DataTypeConfiguration{"
                    + "command="
                    + command
                    + ", event="
                    + event
                    + ", rejection="
                    + rejection
                    + "}";
        }
    }
    public static class DataConfiguration {
        public boolean decision = true;
        public boolean decisionEvaluation = true;
        public boolean decisionRequirements = true;
        public boolean deployment = true;
        public boolean error = true;
        public boolean incident = true;
        public boolean job = true;
        public boolean jobBatch = false;
        public boolean message = true;
        public boolean messageBatch = false;
        public boolean messageSubscription = true;
        public boolean process = true;
        public boolean processInstance = true;
        public boolean processInstanceBatch = false;
        public boolean processInstanceCreation = true;
        public boolean processInstanceModification = true;
        public boolean processMessageSubscription = true;
        public boolean variable = true;
        public boolean variableDocument = true;
        public boolean checkpoint = false;
        public boolean timer = true;
        public boolean messageStartEventSubscription = true;
        public boolean processEvent = false;
        public boolean deploymentDistribution = true;
        public boolean escalation = true;
        public boolean signal = true;
        public boolean signalSubscription = true;
        public boolean resourceDeletion = true;
        public boolean commandDistribution = true;
        public boolean form = true;

        @Override
        public String toString() {
            return "DataConfiguration{"
                    + "decision="
                    + decision
                    + ", decisionEvaluation="
                    + decisionEvaluation
                    + ", decisionRequirements="
                    + decisionRequirements
                    + ", deployment="
                    + deployment
                    + ", error="
                    + error
                    + ", incident="
                    + incident
                    + ", job="
                    + job
                    + ", jobBatch="
                    + jobBatch
                    + ", message="
                    + message
                    + ", messageBatch="
                    + messageBatch
                    + ", messageSubscription="
                    + messageSubscription
                    + ", process="
                    + process
                    + ", processInstance="
                    + processInstance
                    + ", processInstanceBatch="
                    + processInstanceBatch
                    + ", processInstanceCreation="
                    + processInstanceCreation
                    + ", processInstanceModification="
                    + processInstanceModification
                    + ", processMessageSubscription="
                    + processMessageSubscription
                    + ", variable="
                    + variable
                    + ", variableDocument="
                    + variableDocument
                    + ", checkpoint="
                    + checkpoint
                    + ", timer="
                    + timer
                    + ", messageStartEventSubscription="
                    + messageStartEventSubscription
                    + ", processEvent="
                    + processEvent
                    + ", deploymentDistribution="
                    + deploymentDistribution
                    + ", escalation="
                    + escalation
                    + ", signal="
                    + signal
                    + ", signalSubscription="
                    + signalSubscription
                    + ", resourceDeletion="
                    + resourceDeletion
                    + ", commandDistribution="
                    + commandDistribution
                    + ", form="
                    + form
                    + '}';
        }
    }
}
