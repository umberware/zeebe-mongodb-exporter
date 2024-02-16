package io.zeebe.exporter.mongo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.exporter.api.context.Controller;
import io.camunda.zeebe.protocol.record.*;
import io.camunda.zeebe.protocol.record.intent.DeploymentIntent;
import io.camunda.zeebe.protocol.record.value.BpmnElementType;
import io.camunda.zeebe.protocol.record.value.ImmutableProcessInstanceCreationRecordValue;
import io.camunda.zeebe.protocol.record.value.ImmutableProcessInstanceRecordValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MongoExporterUnitTest {
    private MongoExporterClient exporterClient;
    private MongoExporterConfiguration exporterConfiguration;

    private Controller controller;

    @Spy private MongoExporter exporter;

    @BeforeEach()
    public void prepare() {
        this.exporterConfiguration = spy(MongoExporterConfiguration.class);
        this.exporterClient = mock(MongoExporterClient.class);
        this.controller = mock(Controller.class);

        this.exporter.exporterConfiguration = this.exporterConfiguration;
        this.exporter.exporterClient = this.exporterClient;
        this.exporter.controller = this.controller;
    }

    @Test
    public void recordExportable() throws JsonProcessingException {
        Map<String, Object> variables = new HashMap<>();

        variables.put("game", "Cyberpunk 2077");

        ImmutableRecord<RecordValue> record = this.buildRecord(this.buildProcessInstanceCreationRecordValue(variables), RecordType.EVENT, ValueType.PROCESS_INSTANCE_CREATION, 3);
        ObjectMapper mapper = new ObjectMapper();

        String expectedRecordAsJson = mapper.writeValueAsString(record.getValue());
        Map<String, Object> recordAsMap = mapper.readValue(expectedRecordAsJson, new TypeReference<Map<String,Object>>(){});
        recordAsMap.put("intent", record.getIntent());
        recordAsMap.put("recordType", record.getRecordType());
        recordAsMap.put("valueType", record.getValueType());
        recordAsMap.put("timestamp", record.getTimestamp());
        recordAsMap.put("key", record.getKey());
        recordAsMap.put("position", record.getPosition());

        exporter.export(record);

        verify(this.exporterConfiguration).shouldExportEventType(record.getValueType());
        verify(this.exporterConfiguration).shouldExportRecordType(record.getRecordType());
        verify(this.exporterConfiguration, times(1)).getCollectionNameByEvent(eq(record.getValueType()), any());
        verify(this.exporterClient).insertRecord("process-instance-creation", mapper.writeValueAsString(recordAsMap));
    }

    @Test
    public void processInstanceRecord() throws JsonProcessingException {
        RecordValue recordValue = this.buildProcessInstanceRecordValue(BpmnElementType.PROCESS);
        ImmutableRecord<RecordValue> record = this.buildRecord(recordValue, RecordType.EVENT, ValueType.PROCESS_INSTANCE, 3);
        ObjectMapper mapper = new ObjectMapper();

        String expectedRecordAsJson = mapper.writeValueAsString(record.getValue());
        Map<String, Object> recordAsMap = mapper.readValue(expectedRecordAsJson, new TypeReference<Map<String,Object>>(){});
        recordAsMap.put("intent", record.getIntent());
        recordAsMap.put("recordType", record.getRecordType());
        recordAsMap.put("valueType", record.getValueType());
        recordAsMap.put("timestamp", record.getTimestamp());
        recordAsMap.put("key", record.getKey());
        recordAsMap.put("position", record.getPosition());

        exporter.export(record);

        verify(this.exporterConfiguration).shouldExportEventType(record.getValueType());
        verify(this.exporterConfiguration).shouldExportRecordType(record.getRecordType());
        verify(this.exporterConfiguration, times(1)).getCollectionNameByEvent(eq(record.getValueType()), eq(recordValue));
        verify(this.exporterClient).insertRecord("process-instance", mapper.writeValueAsString(recordAsMap));
        verify(this.exporterConfiguration, times(1)).getCollectionNameByEvent(eq(record.getValueType()), eq(recordValue));
        assertEquals("process-instance", this.exporterConfiguration.getCollectionNameByEvent(record.getValueType(), recordValue));
    }

    @Test
    public void processInstanceElementRecord() throws JsonProcessingException {
        Map<String, Object> variables = new HashMap<>();

        variables.put("game", "Cyberpunk 2077");

        RecordValue recordValue = this.buildProcessInstanceRecordValue(BpmnElementType.START_EVENT);
        ImmutableRecord<RecordValue> record = this.buildRecord(recordValue, RecordType.EVENT, ValueType.PROCESS_INSTANCE, 3);
        ObjectMapper mapper = new ObjectMapper();

        String expectedRecordAsJson = mapper.writeValueAsString(record.getValue());
        Map<String, Object> recordAsMap = mapper.readValue(expectedRecordAsJson, new TypeReference<Map<String,Object>>(){});
        recordAsMap.put("intent", record.getIntent());
        recordAsMap.put("recordType", record.getRecordType());
        recordAsMap.put("valueType", record.getValueType());
        recordAsMap.put("timestamp", record.getTimestamp());
        recordAsMap.put("key", record.getKey());
        recordAsMap.put("position", record.getPosition());

        exporter.export(record);

        verify(this.exporterConfiguration).shouldExportEventType(record.getValueType());
        verify(this.exporterConfiguration).shouldExportRecordType(record.getRecordType());
        verify(this.exporterConfiguration, times(1)).getCollectionNameByEvent(eq(record.getValueType()), eq(recordValue));
        verify(this.exporterClient).insertRecord("process-instance-element", mapper.writeValueAsString(recordAsMap));
        verify(this.exporterConfiguration, times(1)).getCollectionNameByEvent(eq(record.getValueType()), eq(recordValue));
        assertEquals("process-instance-element", this.exporterConfiguration.getCollectionNameByEvent(record.getValueType(), recordValue));
    }


    @Test
    public void recordNotExportable() throws JsonProcessingException {
        ImmutableRecord<RecordValue> record = this.buildRecord(null, RecordType.EVENT, ValueType.JOB_BATCH, 1);

        exporter.export(record);

        verify(this.exporterConfiguration).shouldExportEventType(record.getValueType());
        verify(this.exporterConfiguration).shouldExportRecordType(record.getRecordType());
        verify(this.exporterConfiguration, times(0)).getCollectionNameByEvent(eq(record.getValueType()), any(RecordValue.class));
        verify(this.exporterClient, times(0)).insertRecord(anyString(), any());
    }

    protected ImmutableRecord<RecordValue> buildRecord(RecordValue recordValue, RecordType type, ValueType eventType, long position) {
        return ImmutableRecord.builder()
            .withIntent(DeploymentIntent.from((short) 0))
            .withRecordType(type)
            .withPosition(position)
            .withValueType(eventType)
            .withValue(recordValue)
            .withTimestamp(1234567)
            .withKey(12345)
            .withPosition(123456)
            .build();
    }

    protected ImmutableProcessInstanceCreationRecordValue buildProcessInstanceCreationRecordValue(Map<String, Object> variables) {
        return ImmutableProcessInstanceCreationRecordValue.builder()
            .withBpmnProcessId("my-test-process.bpmn")
            .withVersion(1)
            .withVariables(variables)
            .withTenantId("<creation>")
            .withProcessDefinitionKey(12345)
            .withProcessInstanceKey(11234)
            .build();
    }

    protected ImmutableProcessInstanceRecordValue buildProcessInstanceRecordValue(BpmnElementType bpmnElementType) {
        return ImmutableProcessInstanceRecordValue.builder()
                .withBpmnProcessId("my-test-process.bpmn")
                .withVersion(1)
                .withBpmnElementType(bpmnElementType)
                .withTenantId("<creation>")
                .withProcessDefinitionKey(12345)
                .withProcessInstanceKey(11234)
                .build();
    }
}
