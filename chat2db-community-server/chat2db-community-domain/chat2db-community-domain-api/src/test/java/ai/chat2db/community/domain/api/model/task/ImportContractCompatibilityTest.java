package ai.chat2db.community.domain.api.model.task;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImportContractCompatibilityTest {
    private final ObjectMapper jackson = new ObjectMapper();

    @Test
    void legacyImportRecordsKeepUnsetNewFieldsWithBothSerializers() throws Exception {
        String legacy = "{\"taskType\":\"DATA_FILE_IMPORT\",\"sourceFile\":\"orders.csv\",\"format\":\"CSV\"}";
        for (ImportTaskSpec copy : List.of(jackson.readValue(legacy, ImportTaskSpec.class),
                JSON.parseObject(legacy, ImportTaskSpec.class))) {
            assertEquals("orders.csv", copy.getSourceFile());
            assertEquals("CSV", copy.getFormat());
            assertNull(copy.getScope());
            assertNull(copy.getTableSources());
            assertNull(copy.getClientSubmissionId());
            assertNull(copy.getOptions());
            assertNull(copy.getManifestShard());
        }
    }

    @Test
    void richPerSourceOptionsAndSubmissionIdentitySurviveBothSerializers() throws Exception {
        ImportOptions options = ImportOptions.builder().charset("UTF-8").delimiter(";")
                .nullString("\\N").onError("SKIP").maxErrors(3).build();
        ImportTableSource source = ImportTableSource.builder().databaseName("fixture")
                .schemaName("public").tableName("orders").importFileId("opaque-source-id")
                .displayFileName("订单.csv").format("CSV").options(options)
                .columnMappings(List.of(ImportColumnMapping.builder().sourceColumn("编号")
                        .targetColumn("id").build())).build();
        ImportTaskSpec spec = ImportTaskSpec.builder().scope("SCHEMA").mode("STANDARD")
                .clientSubmissionId("retry-same-request").clientSubmissionFingerprint("request-fingerprint")
                .tableSources(List.of(source)).confirmedNoStrongRelations(false).build();
        for (ImportTaskSpec copy : List.of(jackson.readValue(jackson.writeValueAsString(spec), ImportTaskSpec.class),
                JSON.parseObject(JSON.toJSONString(spec), ImportTaskSpec.class))) {
            assertEquals(spec, copy);
            assertEquals(";", copy.getTableSources().get(0).getOptions().getDelimiter());
            assertEquals("opaque-source-id", copy.getTableSources().get(0).getImportFileId());
            assertEquals("编号", copy.getTableSources().get(0).getColumnMappings().get(0).getSourceColumn());
        }
    }

    @Test
    void persistedVersionTwoManifestRetainsItsFingerprintWithBothSerializers() throws Exception {
        ImportManifest manifest = manifest();
        manifest.setManifestFingerprint(ImportManifestIntegrity.calculate(manifest));
        for (ImportManifest copy : List.of(jackson.readValue(jackson.writeValueAsString(manifest), ImportManifest.class),
                JSON.parseObject(JSON.toJSONString(manifest), ImportManifest.class))) {
            assertEquals(manifest, copy);
            ImportManifestIntegrity.requireValid(copy);
        }
    }

    @Test
    void changedSourceChecksumCannotReuseAPersistedManifestFingerprint() {
        ImportManifest manifest = manifest();
        manifest.setManifestFingerprint(ImportManifestIntegrity.calculate(manifest));
        manifest.getShards().get(0).setExpectedChecksum("different-file-checksum");
        assertThrows(IllegalStateException.class, () -> ImportManifestIntegrity.requireValid(manifest));
    }

    @Test
    void changedQualifiedTableCannotReuseAVersionTwoResumeFingerprint() {
        ImportManifest manifest = manifest();
        manifest.setManifestFingerprint(ImportManifestIntegrity.calculate(manifest));
        manifest.getShards().get(0).setSchemaName("other_schema");
        assertThrows(IllegalStateException.class, () -> ImportManifestIntegrity.requireValid(manifest));
    }

    @Test
    void diagnosticShardRolesStayDistinctAndFitTheStorageContract() {
        String shard = "订单😀".repeat(100);
        String first = TaskArtifactRole.rejectForShard(shard);
        assertEquals(first, TaskArtifactRole.rejectForShard(shard));
        assertNotEquals(first, TaskArtifactRole.rejectForShard(shard + "other"));
        assertTrue(first.length() <= 32);
        assertTrue(TaskArtifactRole.isDiagnostic(first));
        assertFalse(TaskArtifactRole.isDiagnostic(TaskArtifactRole.OUTPUT));
        assertFalse(TaskArtifactRole.isDiagnostic("REJECT:"));
        assertFalse(TaskArtifactRole.isDiagnostic(null));
        assertThrows(IllegalArgumentException.class, () -> TaskArtifactRole.rejectForShard(" "));
    }

    private ImportManifest manifest() {
        ImportManifestShard shard = ImportManifestShard.builder().shardId("orders-1")
                .databaseName("fixture").schemaName("public").tableName("orders")
                .tableKey("fixture.public.orders").sourcePath("staged/orders.csv")
                .expectedChecksum("original-checksum").estimatedRows(3).dependencyShardIds(List.of()).build();
        return ImportManifest.builder().schemaVersion(2).taskId(1L).mode(ImportPlanMode.SERIAL_SAFE)
                .sourceFingerprint("source-fingerprint").admissionVerdict("ALLOW")
                .totalEstimatedRows(3).dependencies(List.of()).shards(List.of(shard)).build();
    }
}
