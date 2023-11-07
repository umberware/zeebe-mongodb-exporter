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

    public void initialize() {
        this.mongoClient = MongoClients.create(connection.uri);
        this.mongoDatabase = this.mongoClient.getDatabase(connection.database);
    }

    public boolean shouldExportRecord(RecordType recordType, ValueType valueType) {
        return this.shouldExportEventType(recordType)
                && this.shouldExportRecordType(valueType);
    }

    public boolean shouldExportRecordType(final ValueType valueType) {
        switch (valueType) {
            case DEPLOYMENT:
                return data.deployment;
            case PROCESS:
                return data.process;
            case ERROR:
                return data.error;
            case INCIDENT:
                return data.incident;
            case JOB:
                return data.job;
            case JOB_BATCH:
                return data.jobBatch;
            case MESSAGE:
                return data.message;
            case MESSAGE_BATCH:
                return data.messageBatch;
            case MESSAGE_SUBSCRIPTION:
                return data.messageSubscription;
            case VARIABLE:
                return data.variable;
            case VARIABLE_DOCUMENT:
                return data.variableDocument;
            case PROCESS_INSTANCE:
                return data.processInstance;
            case PROCESS_INSTANCE_BATCH:
                return data.processInstanceBatch;
            case PROCESS_INSTANCE_CREATION:
                return data.processInstanceCreation;
            case PROCESS_INSTANCE_MODIFICATION:
                return data.processInstanceModification;
            case PROCESS_MESSAGE_SUBSCRIPTION:
                return data.processMessageSubscription;
            case DECISION_REQUIREMENTS:
                return data.decisionRequirements;
            case DECISION:
                return data.decision;
            case DECISION_EVALUATION:
                return data.decisionEvaluation;
            case CHECKPOINT:
                return data.checkpoint;
            case TIMER:
                return data.timer;
            case MESSAGE_START_EVENT_SUBSCRIPTION:
                return data.messageStartEventSubscription;
            case PROCESS_EVENT:
                return data.processEvent;
            case DEPLOYMENT_DISTRIBUTION:
                return data.deploymentDistribution;
            case ESCALATION:
                return data.escalation;
            case SIGNAL:
                return data.signal;
            case SIGNAL_SUBSCRIPTION:
                return data.signalSubscription;
            case RESOURCE_DELETION:
                return data.resourceDeletion;
            case COMMAND_DISTRIBUTION:
                return data.commandDistribution;
            case FORM:
                return data.form;
            default:
                return false;
        }
    }

    public boolean shouldExportEventType(final RecordType recordType) {
        switch (recordType) {
            case EVENT:
                return dataType.event;
            case COMMAND:
                return dataType.command;
            case COMMAND_REJECTION:
                return dataType.rejection;
            default:
                return false;
        }
    }

    public String getCollectionNameByEvent(ValueType intent) {
        switch (intent) {
            case DEPLOYMENT:
                return "deployment";
            case INCIDENT:
                return "incident";
            case JOB:
                return "job";
            case PROCESS_INSTANCE:
                return "process-instance";
            case MESSAGE:
                return "message";
            case MESSAGE_SUBSCRIPTION:
                return "message-subscription";
            case MESSAGE_START_EVENT_SUBSCRIPTION:
                return "message-star-event-subscription";
            case PROCESS_MESSAGE_SUBSCRIPTION:
                return "process-message-subscription";
            case JOB_BATCH:
                return "job-batch";
            case TIMER:
                return "timer";
            case VARIABLE:
                return "variable";
            case VARIABLE_DOCUMENT:
                return "variable-document";
            case PROCESS_INSTANCE_CREATION:
                return "process-instance-creation";
            case ERROR:
                return "error";
            case PROCESS:
                return "process";
            case DEPLOYMENT_DISTRIBUTION:
                return "deployment-distribution";
            case PROCESS_EVENT:
                return "process-event";
            case DECISION:
                return "decision";
            case DECISION_REQUIREMENTS:
                return "decision-requirements";
            case DECISION_EVALUATION:
                return "decision-evaluation";
            case CHECKPOINT:
                return "checkpoint";
            case PROCESS_INSTANCE_MODIFICATION:
                return "process-instance-modification";
            default:
                return null;
        }
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
