package ai.chat2db.plugin.db2.builder;

import ai.chat2db.spi.jdbc.DefaultSqlBuilder;
import ai.chat2db.spi.model.Schema;
import org.apache.commons.lang3.StringUtils;

public class DB2SqlBuilder extends DefaultSqlBuilder {


    @Override
    public String pageLimit(String sql, int offset, int pageNo, int pageSize) {
        int startRow = offset + 1;
        int endRow = offset + pageSize;
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 120);
        sqlBuilder.append("SELECT * FROM (SELECT TMP_PAGE.*,ROWNUMBER() OVER() AS CAHT2DB_AUTO_ROW_ID FROM ( \n");
        sqlBuilder.append(sql);
        sqlBuilder.append("\n ) AS TMP_PAGE) TMP_PAGE WHERE CAHT2DB_AUTO_ROW_ID BETWEEN ");
        sqlBuilder.append(startRow);
        sqlBuilder.append(" AND ");
        sqlBuilder.append(endRow);
        return sqlBuilder.toString();
    }

    @Override
    public String buildCreateSchemaSql(Schema schema) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("CREATE SCHEMA \"" + schema.getName() + "\";");

        if (StringUtils.isNotBlank(schema.getComment())) {
            sqlBuilder.append("\nCOMMENT ON SCHEMA \"").append(schema.getName()).append("\" IS '").append(schema.getComment()).append("';");
        }

        return sqlBuilder.toString();
    }



}
