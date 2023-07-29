package ai.chat2db.server.start.test.druid;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;

import ai.chat2db.server.tools.base.excption.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class SqlUtilsTest2 {

    @Test
    public void coment() {
        SQLStatement sqlStatement = SQLUtils.parseSingleStatement(
            "comment on index myindex is '日期xxx';\n",
            DbType.h2);
        log.info("解析sql:{}", sqlStatement);
    }

    @Test
    public void SELECT() {
        SQLStatement sqlStatement = SQLUtils.parseSingleStatement(
            "SELECT * FROM score a left join user b on a.id=b.id LIMIT 10",
            DbType.mysql);

        if (!(sqlStatement instanceof SQLSelectStatement)) {
            throw new BusinessException("dataSource.sqlAnalysisError");
        }
        SQLSelectStatement sqlSelectStatement = (SQLSelectStatement)sqlStatement;
        log.info("解析sql1:{}", sqlSelectStatement);

        log.info("解析sql2:{}", sqlSelectStatement.getSelect().getFirstQueryBlock().getFrom().toString());
    }

    @Test
    public void select2() {
        log.info("tablename:{}",getTable("SELECT * FROM score LIMIT 10"));
        log.info("tablename:{}",getTable("SELECT * FROM score a LIMIT 10"));
        log.info("tablename:{}",getTable("SELECT * FROM score a left join user b on a.id=b.id LIMIT 10"));
    }


    @Test
    public void insert() {
        SQLStatement sqlStatement = SQLUtils.parseSingleStatement("INSERT INTO chat2db.`order` (id, user_id, total_price, created_at, updated_at) VALUES (8, 345, 5601.16, '2022-09-18 11:21:12', '2023-04-30 11:21:12');",
            DbType.mysql);
        log.info("解析sql1:{}", sqlStatement);

    }

    private String getTable(String sql) {
        SQLStatement sqlStatement = SQLUtils.parseSingleStatement(sql,
            DbType.mysql);

        if (!(sqlStatement instanceof SQLSelectStatement)) {
            throw new BusinessException("dataSource.sqlAnalysisError");
        }
        SQLSelectStatement sqlSelectStatement = (SQLSelectStatement)sqlStatement;
        SQLExprTableSource sqlExprTableSource = (SQLExprTableSource)getSQLExprTableSource(
            sqlSelectStatement.getSelect().getFirstQueryBlock().getFrom());
        return sqlExprTableSource.getTableName();
    }

    private SQLTableSource getSQLExprTableSource(SQLTableSource sqlTableSource) {
        if (sqlTableSource instanceof SQLExprTableSource sqlExprTableSource) {
            return sqlExprTableSource;
        } else if (sqlTableSource instanceof SQLJoinTableSource sqlJoinTableSource) {
            return getSQLExprTableSource(sqlJoinTableSource.getLeft());
        }
        return null;
    }

}
