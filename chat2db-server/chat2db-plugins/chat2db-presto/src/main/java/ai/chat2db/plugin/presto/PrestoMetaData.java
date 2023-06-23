package ai.chat2db.plugin.presto;

import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.jdbc.DefaultMetaService;

public class PrestoMetaData extends DefaultMetaService implements MetaData {
    public String tableDDL(String databaseName, String schemaName,String tableName) {
        return "";
    }
}
