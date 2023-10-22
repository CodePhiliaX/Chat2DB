package ai.chat2db.plugin.db2.builder;

import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.jdbc.DefaultSqlBuilder;

public class DB2SqlBuilder extends DefaultSqlBuilder {



    @Override
    public String pageLimit(String sql, int offset, int pageNo, int pageSize) {
        int startRow = offset + 1;
        int endRow = offset+ pageSize;
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 120);
        sqlBuilder.append("SELECT * FROM (SELECT TMP_PAGE.*,ROWNUMBER() OVER() AS PAGEHELPER_ROW_ID FROM ( \n");
        sqlBuilder.append(sql);
        sqlBuilder.append("\n ) AS TMP_PAGE) TMP_PAGE WHERE PAGEHELPER_ROW_ID BETWEEN ");
        sqlBuilder.append(startRow);
        sqlBuilder.append(" AND ");
        sqlBuilder.append(endRow);
        return sqlBuilder.toString();
    }
}
