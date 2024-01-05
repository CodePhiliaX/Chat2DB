package ai.chat2db.spi.jdbc;

import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.model.Database;
import ai.chat2db.spi.model.OrderBy;
import ai.chat2db.spi.model.Schema;
import ai.chat2db.spi.model.Table;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class DefaultSqlBuilder implements SqlBuilder {


    @Override
    public String buildCreateTableSql(Table table) {
        return null;
    }

    @Override
    public String buildModifyTaleSql(Table oldTable, Table newTable) {
        return null;
    }

    @Override
    public String pageLimit(String sql, int offset, int pageNo, int pageSize) {
        return null;
    }

    public static String CREATE_DATABASE_SQL = "CREATE DATABASE IF NOT EXISTS `%s` DEFAULT CHARACTER SET %s COLLATE %s";

    @Override
    public String buildCreateDatabaseSql(Database database) {
        return null;
    }

    @Override
    public String buildModifyDatabaseSql(Database oldDatabase, Database newDatabase) {
        return null;
    }

    @Override
    public String buildCreateSchemaSql(Schema schema) {
        return null;
    }

    @Override
    public String buildModifySchemaSql(String oldSchemaName, String newSchemaName) {
        return null;
    }

    @Override
    public String buildOrderBySql(String originSql, List<OrderBy> orderByList) {
        if(CollectionUtils.isEmpty(orderByList)){
            return originSql;
        }
        try {
            Statement statement = CCJSqlParserUtil.parse(originSql);
            if (statement instanceof Select) {
                Select selectStatement = (Select) statement;
                PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();

                // 创建新的 ORDER BY 子句
                List<OrderByElement> orderByElements = new ArrayList<>();

                for (OrderBy orderBy : orderByList) {
                    OrderByElement orderByElement = new OrderByElement();
                    orderByElement.setExpression(CCJSqlParserUtil.parseExpression(orderBy.getColumnName()));
                    orderByElement.setAsc(orderBy.isAsc()); // 设置为升序，使用 setAsc(false) 设置为降序
                    orderByElements.add(orderByElement);
                }
                // 替换原有的 ORDER BY 子句
                plainSelect.setOrderByElements(orderByElements);
                // 输出修改后的 SQL
                return plainSelect.toString();
            }
        } catch (Exception e) {
        }
        return originSql;
    }
}
