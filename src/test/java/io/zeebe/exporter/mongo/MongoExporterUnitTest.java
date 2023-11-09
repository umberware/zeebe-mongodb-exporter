package io.zeebe.exporter.mongo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.protocol.record.*;
import io.camunda.zeebe.protocol.record.intent.DeploymentIntent;
import io.camunda.zeebe.protocol.record.value.ImmutableProcessInstanceCreationRecordValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MongoExporterUnitTest {
    private MongoExporterClient exporterClient;
    private MongoExporterConfiguration exporterConfiguration;

    @Spy private MongoExporter exporter;

    @BeforeEach()
    public void prepare() {
        this.exporterConfiguration = spy(MongoExporterConfiguration.class);
        this.exporterClient = mock(MongoExporterClient.class);

        this.exporter.exporterConfiguration = this.exporterConfiguration;
        this.exporter.exporterClient = this.exporterClient;
    }

    @Test
    public void recordExportable() throws JsonProcessingException {
        Map<String, Object> variables = new HashMap<>();

        variables.put("game", "Cyberpunk 2077");

        ImmutableRecord<RecordValue> record = this.buildRecord(this.buildProcessInstanceCreationRecordValue(variables), RecordType.EVENT, ValueType.PROCESS_INSTANCE);
        ObjectMapper mapper = new ObjectMapper();

        String expectedRecordAsJson = mapper.writeValueAsString(record.getValue());
        Map<String, Object> recordAsMap = mapper.readValue(expectedRecordAsJson, new TypeReference<Map<String,Object>>(){});
        recordAsMap.put("intent", record.getIntent());
        recordAsMap.put("recordType", record.getRecordType());
        recordAsMap.put("valueType", record.getValueType());

        exporter.export(record);

        verify(this.exporterConfiguration).shouldExportEventType(record.getValueType());
        verify(this.exporterConfiguration).shouldExportRecordType(record.getRecordType());
        verify(this.exporterClient).insertRecord("process-instance", mapper.writeValueAsString(recordAsMap));
    }

    @Test
    public void recordNotExportable() throws JsonProcessingException {
        ImmutableRecord<RecordValue> record = this.buildRecord(null, RecordType.EVENT, ValueType.JOB_BATCH);

        exporter.export(record);

        verify(this.exporterConfiguration).shouldExportEventType(record.getValueType());
        verify(this.exporterConfiguration).shouldExportRecordType(record.getRecordType());
        verify(this.exporterConfiguration, times(0)).getCollectionNameByEvent(record.getValueType());
        verify(this.exporterClient, times(0)).insertRecord(anyString(), any());
    }

    protected ImmutableRecord<RecordValue> buildRecord(RecordValue recordValue, RecordType type, ValueType eventType) {
        return ImmutableRecord.builder()
            .withIntent(DeploymentIntent.from((short) 0))
            .withRecordType(type)
            .withValueType(eventType)
            .withValue(recordValue)
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
}
