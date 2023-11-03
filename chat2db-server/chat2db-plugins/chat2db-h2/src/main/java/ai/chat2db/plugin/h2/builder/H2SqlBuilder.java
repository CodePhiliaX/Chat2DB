package ai.chat2db.plugin.h2.builder;

import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.jdbc.DefaultSqlBuilder;
import ai.chat2db.spi.model.Schema;
import org.apache.commons.lang3.StringUtils;

public class H2SqlBuilder extends DefaultSqlBuilder implements SqlBuilder {

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
