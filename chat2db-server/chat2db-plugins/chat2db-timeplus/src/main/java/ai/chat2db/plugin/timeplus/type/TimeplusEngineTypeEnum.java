package ai.chat2db.plugin.timeplus.type;

import ai.chat2db.spi.model.EngineType;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public enum TimeplusEngineTypeEnum {
    Stream("Stream", true, true, true, false, true, true, true, true),
    ExternalStream(
        "ExternalStream",
        false,
        false,
        false,
        false,
        true,
        false,
        false,
        false
    ),
    ExternalTable(
        "ExternalTable",
        false,
        false,
        false,
        false,
        true,
        false,
        false,
        false
    ),
    MutableStream(
        "MutableStream",
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true
    ),
    View("View", false, false, false, false, false, false, false, false),
    MaterializedView(
        "MaterializedView",
        true,
        false,
        false,
        false,
        true,
        false,
        false,
        false
    ),
    Random("Random", false, false, false, false, true, false, false, false),
    Dictionary(
        "Dictionary",
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false
    ),
    S3("S3", false, true, false, false, true, false, false, false),
    MergeTree("MergeTree", true, true, true, false, true, true, true, false),
    ReplicatedReplacingMergeTree(
        "ReplicatedReplacingMergeTree",
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true
    ),
    Memory("Memory", false, false, false, false, true, true, false, false),
    ReplacingMergeTree(
        "ReplacingMergeTree",
        true,
        true,
        true,
        false,
        true,
        true,
        true,
        false
    ),
    ReplicatedAggregatingMergeTree(
        "ReplicatedAggregatingMergeTree",
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true
    ),
    ReplicatedMergeTree(
        "ReplicatedMergeTree",
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true
    ),
    ReplicatedCollapsingMergeTree(
        "ReplicatedCollapsingMergeTree",
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true
    ),
    File("File", false, false, false, false, true, false, false, false),
    SummingMergeTree(
        "SummingMergeTree",
        true,
        true,
        true,
        false,
        true,
        true,
        true,
        false
    ),
    CollapsingMergeTree(
        "CollapsingMergeTree",
        true,
        true,
        true,
        false,
        true,
        true,
        true,
        false
    ),
    Merge("Merge", false, false, false, false, false, false, false, false),
    AggregatingMergeTree(
        "AggregatingMergeTree",
        true,
        true,
        true,
        false,
        true,
        true,
        true,
        false
    ),
    Null("Null", false, false, false, false, false, true, false, false),

    Log("Log", false, false, false, false, true, false, false, false);

    private static Map<String, TimeplusEngineTypeEnum> ENGINE_TYPE_MAP =
        Maps.newHashMap();

    static {
        for (TimeplusEngineTypeEnum value : TimeplusEngineTypeEnum.values()) {
            ENGINE_TYPE_MAP.put(value.getEngineType().getName(), value);
        }
    }

    private EngineType engineType;

    TimeplusEngineTypeEnum(
        String name,
        boolean supportTTL,
        boolean supportSortOrder,
        boolean supportSkippingIndices,
        boolean supportDeduplication,
        boolean supportSettings,
        boolean supportParallelInsert,
        boolean supportProjections,
        boolean supportReplication
    ) {
        this.engineType = new EngineType(
            name,
            supportTTL,
            supportSortOrder,
            supportSkippingIndices,
            supportDeduplication,
            supportSettings,
            supportParallelInsert,
            supportProjections,
            supportReplication
        );
    }

    public static TimeplusEngineTypeEnum getByType(String dataType) {
        return ENGINE_TYPE_MAP.get(dataType.toUpperCase());
    }

    public static List<EngineType> getTypes() {
        return Arrays.stream(TimeplusEngineTypeEnum.values())
            .map(engineTypeEnum -> engineTypeEnum.getEngineType())
            .toList();
    }

    public EngineType getEngineType() {
        return engineType;
    }
}
