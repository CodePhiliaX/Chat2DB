package ai.chat2db.plugin.clickhouse.type;

import ai.chat2db.spi.model.EngineType;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public enum ClickHouseEngineTypeEnum {
    AzureBlobStorage("AzureBlobStorage",false,true,false,false,true,false,false,false                                                ),
    KeeperMap("KeeperMap",false,true,false,false,false,true,false,false                                                               ),
    SQLite("SQLite",false,false,false,false,false,false,false,false                                                                ),
    ExternalDistributed("ExternalDistributed",false,false,false,false,false,false,false,false                                       ),
    PostgreSQL("PostgreSQL",false,false,false,false,false,false,false,false                                                        ),
    NATS("NATS",false,false,false,false,true,false,false,false                                                                       ),
    RabbitMQ("RabbitMQ",false,false,false,false,true,false,false,false                                                               ),
    Kafka("Kafka",false,false,false,false,true,false,false,false                                                                      ),
    MongoDB("MongoDB",false,false,false,false,false,false,false,false                                                               ),
    FileLog("FileLog",false,false,false,false,true,false,false,false                                                                ),
    Dictionary("Dictionary",false,false,false,false,false,false,false,false                                                        ),
    MySQL("MySQL",false,false,false,false,true,false,false,false                                                                      ),
    S3Queue("S3Queue",false,false,false,false,true,false,false,false                                                                ),
    HDFS("HDFS",false,true,false,false,false,false,false,false                                                                       ),
    MaterializedPostgreSQL("MaterializedPostgreSQL",false,true,false,false,true,false,false,false                                  ),
    S3("S3",false,true,false,false,true,false,false,false                                                                          ),
    FuzzJSON("FuzzJSON",false,false,false,false,false,false,false,false                                                              ),
    OSS("OSS",false,true,false,false,true,false,false,false                                                                         ),
    WindowView("WindowView",false,false,false,false,false,false,false,false                                                        ),
    Distributed("Distributed",false,false,false,false,true,true,false,false                                                         ),
    ReplicatedSummingMergeTree("ReplicatedSummingMergeTree",true,true,true,true,true,true,true,true                                ),
    ExecutablePool("ExecutablePool",false,false,false,false,true,false,false,false                                                 ),
    COSN("COSN",false,true,false,false,true,false,false,false                                                                        ),
    Iceberg("Iceberg",false,false,false,false,false,false,false,false                                                               ),
    MaterializedView("MaterializedView",false,false,false,false,false,false,false,false                                              ),
    View("View",false,false,false,false,false,false,false,false                                                                      ),
    JDBC("JDBC",false,false,false,false,false,false,false,false                                                                      ),
    Join("Join",false,false,false,false,true,false,false,false                                                                       ),
    Executable("Executable",false,false,false,false,true,false,false,false                                                         ),
    Set("Set",false,false,false,false,true,false,false,false                                                                        ),
    Redis("Redis",false,true,false,false,false,true,false,false                                                                       ),
    GenerateRandom("GenerateRandom",false,false,false,false,false,false,false,false                                                ),
    LiveView("LiveView",false,false,false,false,false,false,false,false                                                              ),
    MergeTree("MergeTree",true,true,true,false,true,true,true,false                                                                   ),
    ReplicatedReplacingMergeTree("ReplicatedReplacingMergeTree",true,true,true,true,true,true,true,true                              ),
    Memory("Memory",false,false,false,false,true,true,false,false                                                                  ),
    Buffer("Buffer",false,false,false,false,false,true,false,false                                                                 ),
    URL("URL",false,false,false,false,true,false,false,false                                                                        ),
    ReplicatedVersionedCollapsingMergeTree("ReplicatedVersionedCollapsingMergeTree",true,true,true,true,true,true,true,true        ),
    VersionedCollapsingMergeTree("VersionedCollapsingMergeTree",true,true,true,false,true,true,true,false                            ),
    Hive("Hive",false,true,false,false,true,false,false,false                                                                        ),
    ReplacingMergeTree("ReplacingMergeTree",true,true,true,false,true,true,true,false                                              ),
    ReplicatedAggregatingMergeTree("ReplicatedAggregatingMergeTree",true,true,true,true,true,true,true,true                        ),
    ReplicatedMergeTree("ReplicatedMergeTree",true,true,true,true,true,true,true,true                                               ),
    DeltaLake("DeltaLake",false,false,false,false,false,false,false,false                                                             ),
    EmbeddedRocksDB("EmbeddedRocksDB",true,true,false,false,false,true,false,false                                                  ),
    ReplicatedCollapsingMergeTree("ReplicatedCollapsingMergeTree",true,true,true,true,true,true,true,true                             ),
    File("File",false,false,false,false,true,false,false,false                                                                       ),
    TinyLog("TinyLog",false,false,false,false,true,false,false,false                                                                ),
    ReplicatedGraphiteMergeTree("ReplicatedGraphiteMergeTree",true,true,true,true,true,true,true,true                               ),
    SummingMergeTree("SummingMergeTree",true,true,true,false,true,true,true,false                                                    ),
    Hudi("Hudi",false,false,false,false,false,false,false,false                                                                      ),
    GraphiteMergeTree("GraphiteMergeTree",true,true,true,false,true,true,true,false                                                   ),
    CollapsingMergeTree("CollapsingMergeTree",true,true,true,false,true,true,true,false                                             ),
    Merge("Merge",false,false,false,false,false,false,false,false                                                                     ),
    AggregatingMergeTree("AggregatingMergeTree",true,true,true,false,true,true,true,false                                            ),
    ODBC("ODBC",false,false,false,false,false,false,false,false                                                                      ),
    Null("Null",false,false,false,false,false,true,false,false                                                                       ),
    StripeLog("StripeLog",false,false,false,false,true,false,false,false                                                              ),
    Log("Log",false,false,false,false,true,false,false,false                                                                        ),

    ;
    private static Map<String, ClickHouseEngineTypeEnum> ENGINE_TYPE_MAP = Maps.newHashMap();

    static {
        for (ClickHouseEngineTypeEnum value : ClickHouseEngineTypeEnum.values()) {
            ENGINE_TYPE_MAP.put(value.getEngineType().getName(), value);
        }
    }

    private EngineType engineType;


    ClickHouseEngineTypeEnum(String name, boolean supportTTL, boolean supportSortOrder, boolean supportSkippingIndices, boolean supportDeduplication, boolean supportSettings, boolean supportParallelInsert, boolean supportProjections, boolean supportReplication) {
        this.engineType = new EngineType(name, supportTTL, supportSortOrder, supportSkippingIndices, supportDeduplication, supportSettings, supportParallelInsert, supportProjections, supportReplication);
    }

    public static ClickHouseEngineTypeEnum getByType(String dataType) {
        return ENGINE_TYPE_MAP.get(dataType.toUpperCase());
    }

    public static List<EngineType> getTypes() {
        return Arrays.stream(ClickHouseEngineTypeEnum.values()).map(engineTypeEnum ->
                engineTypeEnum.getEngineType()
        ).toList();
    }

    public EngineType getEngineType() {
        return engineType;
    }
}
