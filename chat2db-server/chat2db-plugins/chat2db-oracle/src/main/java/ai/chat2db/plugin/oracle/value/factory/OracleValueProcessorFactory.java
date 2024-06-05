package ai.chat2db.plugin.oracle.value.factory;

import ai.chat2db.plugin.oracle.type.OracleColumnTypeEnum;
import ai.chat2db.plugin.oracle.value.sub.*;
import ai.chat2db.spi.jdbc.DefaultValueProcessor;

import java.util.Map;

/**
 * @author: zgq
 * @date: 2024年06月03日 23:21
 */  // TODO: 1.空间数据类型 2.XML数据类型 3.动态类型数据 4.ANSI、DB2 和 SQL/DS 数据
public class OracleValueProcessorFactory {

    private static final Map<String, DefaultValueProcessor> PROCESSOR_MAP;

    static {
        OracleClobProcessor oracleClobProcessor = new OracleClobProcessor();
        OracleTimeStampProcessor oracleTimeStampProcessor = new OracleTimeStampProcessor();
        PROCESSOR_MAP = Map.ofEntries(
                //clob
                Map.entry(OracleColumnTypeEnum.CLOB.name(), oracleClobProcessor),
                Map.entry(OracleColumnTypeEnum.NCLOB.name(), oracleClobProcessor),
                Map.entry(OracleColumnTypeEnum.LONG.name(), oracleClobProcessor),
                //date
                Map.entry(OracleColumnTypeEnum.DATE.name(), new OracleDateProcessor()),
                //timestamp
                Map.entry(OracleColumnTypeEnum.TIMESTAMP.name(), oracleTimeStampProcessor),
                Map.entry(OracleColumnTypeEnum.TIMESTAMP_WITH_LOCAL_TIME_ZONE.getColumnType().getTypeName(), oracleTimeStampProcessor),
                Map.entry(OracleColumnTypeEnum.TIMESTAMP_WITH_TIME_ZONE.getColumnType().getTypeName(), new OracleTimeStampTZProcessor()),
                //INTERVAL
                Map.entry("INTERVALDS", new OracleIntervalDSProcessor()),
                Map.entry("INTERVALYM", new OracleIntervalYMProcessor()),
                //number
                Map.entry(OracleColumnTypeEnum.NUMBER.name(), new OracleNumberProcessor()),
                //blob
                Map.entry(OracleColumnTypeEnum.BLOB.name(), new OracleBlobProcessor())
        );

    }

    public static DefaultValueProcessor getValueProcessor(String type) {
        DefaultValueProcessor processor = PROCESSOR_MAP.get(type);
        return processor == null ? new DefaultValueProcessor() : processor;
    }

}
