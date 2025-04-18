package ai.chat2db.plugin.postgresql.builder;

import ai.chat2db.plugin.postgresql.type.PostgreSQLColumnTypeEnum;
import ai.chat2db.plugin.postgresql.type.PostgreSQLIndexTypeEnum;
import ai.chat2db.spi.jdbc.DefaultSqlBuilder;
import ai.chat2db.spi.model.*;
import ai.chat2db.spi.sql.Chat2DBContext;
import cn.hutool.core.util.BooleanUtil;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ai.chat2db.plugin.postgresql.consts.SequenceCommonConst.*;
import static ai.chat2db.server.tools.base.constant.SymbolConstant.*;


public class PostgreSQLSqlBuilder extends DefaultSqlBuilder {
    @Override
    public String buildCreateTableSql(Table table) {
        StringBuilder script = new StringBuilder();
        script.append("CREATE TABLE ");
        script.append("\"").append(table.getName()).append("\"").append(" (").append(" ").append("\n");
        // append column
        for (TableColumn column : table.getColumnList()) {
            if (StringUtils.isBlank(column.getName()) || StringUtils.isBlank(column.getColumnType())) {
                continue;
            }
            PostgreSQLColumnTypeEnum typeEnum = PostgreSQLColumnTypeEnum.getByType(column.getColumnType());
            if(typeEnum == null){
                continue;
            }
            script.append("\t").append(typeEnum.buildCreateColumnSql(column)).append(",\n");
        }
        Map<Boolean, List<TableIndex>> tableIndexMap = table.getIndexList().stream()
                .collect(Collectors.partitioningBy(v -> PostgreSQLIndexTypeEnum.NORMAL.getName().equals(v.getType())));
        // append constraint key
        List<TableIndex> constraintList = tableIndexMap.get(Boolean.FALSE);
        if (CollectionUtils.isNotEmpty(constraintList)) {
            for (TableIndex index : constraintList) {
                if (StringUtils.isBlank(index.getName()) || StringUtils.isBlank(index.getType())) {
                    continue;
                }
                PostgreSQLIndexTypeEnum indexTypeEnum = PostgreSQLIndexTypeEnum.getByType(index.getType());
                if(indexTypeEnum == null){
                    continue;
                }
                script.append("\t").append("").append(indexTypeEnum.buildIndexScript(index));
                script.append(",\n");
            }

        }
        script = new StringBuilder(script.substring(0, script.length() - 2));
        script.append("\n)").append(";");

        // append index
        List<TableIndex> tableIndexList = tableIndexMap.get(Boolean.TRUE);
        for (TableIndex tableIndex : tableIndexList) {
            if (StringUtils.isBlank(tableIndex.getName()) || StringUtils.isBlank(tableIndex.getType())) {
                continue;
            }
            script.append("\n");
            PostgreSQLIndexTypeEnum indexTypeEnum = PostgreSQLIndexTypeEnum.getByType(tableIndex.getType());
            if(indexTypeEnum == null){
                continue;
            }
            script.append("").append(indexTypeEnum.buildIndexScript(tableIndex)).append(";");
        }

        // append comment
        if (StringUtils.isNotBlank(table.getComment())) {
            script.append("\n");
            script.append("COMMENT ON TABLE").append(" ").append("\"").append(table.getName()).append("\" IS '")
                    .append(table.getComment()).append("';\n");
        }
        List<TableColumn> tableColumnList = table.getColumnList().stream().filter(v -> StringUtils.isNotBlank(v.getComment())).toList();
        for (TableColumn tableColumn : tableColumnList) {
            PostgreSQLColumnTypeEnum typeEnum = PostgreSQLColumnTypeEnum.getByType(tableColumn.getColumnType());
            if(typeEnum == null){
                continue;
            }
            script.append(typeEnum.buildComment(tableColumn, typeEnum)).append("\n");
            ;
        }
        List<TableIndex> indexList = table.getIndexList().stream().filter(v -> StringUtils.isNotBlank(v.getComment())).toList();
        for (TableIndex index : indexList) {
            PostgreSQLIndexTypeEnum indexEnum = PostgreSQLIndexTypeEnum.getByType(index.getType());
            if(indexEnum == null){
                continue;
            }
            script.append(indexEnum.buildIndexComment(index)).append("\n");
            ;
        }

        return script.toString();
    }

    @Override
    public String buildModifyTaleSql(Table oldTable, Table newTable) {
        StringBuilder script = new StringBuilder();
        if (!StringUtils.equalsIgnoreCase(oldTable.getName(), newTable.getName())) {
            script.append("ALTER TABLE ").append("\"").append(oldTable.getName()).append("\"");
            script.append("\t").append("RENAME TO ").append("\"").append(newTable.getName()).append("\"").append(";\n");

        }
        newTable.setColumnList(newTable.getColumnList().stream().filter(v -> StringUtils.isNotBlank(v.getEditStatus())).toList());
        newTable.setIndexList(newTable.getIndexList().stream().filter(v -> StringUtils.isNotBlank(v.getEditStatus())).toList());

        //update name
        List<TableColumn> columnNameList = newTable.getColumnList().stream().filter(v ->
                v.getOldName() != null && !StringUtils.equals(v.getOldName(), v.getName())).toList();
        for (TableColumn tableColumn : columnNameList) {
            script.append("ALTER TABLE ").append("\"").append(newTable.getName()).append("\" ").append("RENAME COLUMN \"")
                    .append(tableColumn.getOldName()).append("\" TO \"").append(tableColumn.getName()).append("\";\n");
        }

        Map<Boolean, List<TableIndex>> tableIndexMap = newTable.getIndexList().stream()
                .collect(Collectors.partitioningBy(v -> PostgreSQLIndexTypeEnum.NORMAL.getName().equals(v.getType())));
        StringBuilder scriptModify = new StringBuilder();
        Boolean modify = false;
        scriptModify.append("ALTER TABLE ").append("\"").append(newTable.getName()).append("\" \n");
        // append modify column
        for (TableColumn tableColumn : newTable.getColumnList()) {
            PostgreSQLColumnTypeEnum typeEnum = PostgreSQLColumnTypeEnum.getByType(tableColumn.getColumnType());
            if(typeEnum == null){
                continue;
            }
            scriptModify.append("\t").append(typeEnum.buildModifyColumn(tableColumn)).append(",\n");
            modify = true;

        }

        // append modify constraint
        for (TableIndex tableIndex : tableIndexMap.get(Boolean.FALSE)) {
            if (StringUtils.isNotBlank(tableIndex.getType())) {
                PostgreSQLIndexTypeEnum indexTypeEnum = PostgreSQLIndexTypeEnum.getByType(tableIndex.getType());
                if(indexTypeEnum == null){
                    continue;
                }
                scriptModify.append("\t").append(indexTypeEnum.buildModifyIndex(tableIndex)).append(",\n");
                modify = true;
            }
        }

        if (BooleanUtils.isTrue(modify)) {
            script.append(scriptModify);
            script = new StringBuilder(script.substring(0, script.length() - 2));
            script.append(";\n");
        }

        // append modify index
        for (TableIndex tableIndex : tableIndexMap.get(Boolean.TRUE)) {
            if (StringUtils.isNotBlank(tableIndex.getEditStatus()) && StringUtils.isNotBlank(tableIndex.getType())) {
                PostgreSQLIndexTypeEnum indexTypeEnum = PostgreSQLIndexTypeEnum.getByType(tableIndex.getType());
                if(indexTypeEnum == null){
                    continue;
                }
                script.append(indexTypeEnum.buildModifyIndex(tableIndex)).append(";\n");
            }
        }

        // append comment
        if (!StringUtils.equals(oldTable.getComment(), newTable.getComment())) {
            script.append("\n");
            script.append("COMMENT ON TABLE").append(" ").append("\"").append(newTable.getName()).append("\" IS '")
                    .append(newTable.getComment()).append("';\n");
        }
        for (TableColumn tableColumn : newTable.getColumnList()) {
            PostgreSQLColumnTypeEnum typeEnum = PostgreSQLColumnTypeEnum.getByType(tableColumn.getColumnType());
            if(typeEnum ==null){
                continue;
            }
            script.append(typeEnum.buildComment(tableColumn, typeEnum)).append("\n");
            ;
        }
        List<TableIndex> indexList = newTable.getIndexList().stream().filter(v -> StringUtils.isNotBlank(v.getComment())).toList();
        for (TableIndex index : indexList) {
            PostgreSQLIndexTypeEnum indexEnum = PostgreSQLIndexTypeEnum.getByType(index.getType());
            if(indexEnum == null){
                continue;
            }
            script.append(indexEnum.buildIndexComment(index)).append("\n");
        }

        return script.toString();
    }

    @Override
    public String pageLimit(String sql, int offset, int pageNo, int pageSize) {
        StringBuilder sqlStr = new StringBuilder(sql.length() + 17);
        sqlStr.append(sql);
        if (offset == 0) {
            sqlStr.append(" LIMIT ");
            sqlStr.append(pageSize);
        } else {
            sqlStr.append(" LIMIT ");
            sqlStr.append(pageSize);
            sqlStr.append(" OFFSET ");
            sqlStr.append(offset);
        }
        return sqlStr.toString();
    }

    @Override
    public String buildCreateDatabaseSql(Database database) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("CREATE DATABASE \""+database.getName()+"\"");
        sqlBuilder.append("\nWITH ");
        if(StringUtils.isNotBlank(database.getCharset())){
            sqlBuilder.append("\n LC_CTYPE = '").append(database.getCharset()).append("' ");
        }
        if(StringUtils.isNotBlank(database.getCollation())){
            sqlBuilder.append("\n LC_COLLATE = '").append(database.getCollation()).append("' ");
        }

        if(StringUtils.isNotBlank(database.getComment())){
            sqlBuilder.append("; COMMENT ON DATABASE \"").append(database.getName()).append("\" IS '").append(database.getComment()).append("';");
        }
        return sqlBuilder.toString();
    }


    @Override
    public String buildCreateSchemaSql(Schema schema){
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("CREATE SCHEMA \""+schema.getName()+"\"");
        if(StringUtils.isNotBlank(schema.getOwner())){
            sqlBuilder.append(" AUTHORIZATION ").append(schema.getOwner());
        }
        if(StringUtils.isNotBlank(schema.getComment())){
            sqlBuilder.append("; COMMENT ON SCHEMA \"").append(schema.getName()).append("\" IS '").append(schema.getComment()).append("';");
        }
        return sqlBuilder.toString();
    }

    @Override
    @SneakyThrows
    public String buildCreateSequenceSql(Sequence sequence) {

        double databaseProductVersion = Double.parseDouble(Chat2DBContext.getConnection().getMetaData().getDatabaseProductVersion());
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(CREATE_SEQUENCE).append(getMetaDataName(sequence.getNspname(), sequence.getRelname())).append("\n ");
        if (Double.compare(databaseProductVersion, 10.0) >= 0) {
            sqlBuilder.append(AS).append(sequence.getTypname()).append("\n ");
        }
        Optional.ofNullable(sequence.getSeqstart()).ifPresent(v -> sqlBuilder.append(START_WITH).append(v).append("\n "));

        Optional.ofNullable(sequence.getSeqincrement()).ifPresent(v -> sqlBuilder.append(INCREMENT_BY).append(v).append("\n "));

        Optional.ofNullable(sequence.getSeqmin()).ifPresent(v -> sqlBuilder.append(MINVALUE).append(v).append("\n "));

        Optional.ofNullable(sequence.getSeqmax()).ifPresent(v -> sqlBuilder.append(MAXVALUE).append(v).append("\n "));

        Optional.ofNullable(sequence.getSeqcache()).ifPresent(v -> sqlBuilder.append(CACHE).append(v).append("\n "));

        Optional.ofNullable(sequence.getSeqcycle()).ifPresent(v -> {
            if (Boolean.TRUE.equals(sequence.getSeqcycle())) {
                sqlBuilder.append(CYCLE).append("\n ");
            }
        });

        sqlBuilder.append(SEMICOLON).append("\n ").append("\n ");

        Optional.ofNullable(sequence.getComment()).ifPresent(v -> sqlBuilder.append(COMMENT_ON_SEQUENCE)
                .append(getMetaDataName(sequence.getNspname(), sequence.getRelname()))
                .append(IS).append(SQUOT).append(v).append(SQUOT).append(SEMICOLON).append("\n ").append("\n "));

        Optional.ofNullable(sequence.getRolname()).ifPresent(v -> sqlBuilder.append(ALTER_SEQUENCE)
                .append(getMetaDataName(sequence.getNspname(), sequence.getRelname()))
                .append(OWNER_TO).append(getMetaDataName(v)).append(SEMICOLON));
        return sqlBuilder.toString();
    }

    @Override
    public String buildModifySequenceSql(Sequence oldSequence, Sequence newSequence) {
        StringBuilder sqlBuilder = new StringBuilder();
        if (!StringUtils.equalsIgnoreCase(oldSequence.getRelname(), newSequence.getRelname())) {
            sqlBuilder.append(ALTER_SEQUENCE).append(getMetaDataName(oldSequence.getNspname(), oldSequence.getRelname())).append(RENAME_TO).append(getMetaDataName(newSequence.getRelname())).append(SEMICOLON).append(BLANK_LINE);
        }
        if (!StringUtils.equals(oldSequence.getComment(), newSequence.getComment())) {
            sqlBuilder.append(COMMENT_ON_SEQUENCE).append(getMetaDataName(newSequence.getNspname(), newSequence.getRelname())).append(IS).append(SQUOT).append(newSequence.getComment()).append(SQUOT).append(SEMICOLON).append(BLANK_LINE);
        }
        if (!StringUtils.equals(oldSequence.getSeqcache(), newSequence.getSeqcache())) {
            sqlBuilder.append(ALTER_SEQUENCE).append(getMetaDataName(newSequence.getNspname(), newSequence.getRelname())).append(CACHE).append(getMetaDataName(newSequence.getSeqcache())).append(SEMICOLON).append(BLANK_LINE);
        }
        if (BooleanUtil.xor(oldSequence.getSeqcycle(), newSequence.getSeqcycle())) {
            if (Boolean.TRUE.equals(newSequence.getSeqcycle())) {
                sqlBuilder.append(ALTER_SEQUENCE).append(getMetaDataName(newSequence.getNspname(), newSequence.getRelname())).append(CYCLE).append(BLANK_LINE);
            } else {
                sqlBuilder.append(ALTER_SEQUENCE).append(getMetaDataName(newSequence.getNspname(), newSequence.getRelname())).append(NO_CYCLE).append(BLANK_LINE);
            }
        }

        if (!StringUtils.equals(oldSequence.getSeqstart(), newSequence.getSeqstart()) ||
                !StringUtils.equals(oldSequence.getSeqincrement(), newSequence.getSeqincrement()) ||
                !StringUtils.equals(oldSequence.getSeqmax(), newSequence.getSeqmax()) ||
                !StringUtils.equals(oldSequence.getSeqmin(), newSequence.getSeqmin())) {
            sqlBuilder.append(ALTER_SEQUENCE);
            if (!StringUtils.equals(oldSequence.getSeqstart(), newSequence.getSeqstart())) {
                sqlBuilder.append(getMetaDataName(newSequence.getNspname(), newSequence.getRelname())).append(RESTART_WITH).append(newSequence.getSeqstart());
            }
            if (!StringUtils.equals(oldSequence.getSeqincrement(), newSequence.getSeqincrement())) {
                sqlBuilder.append(INCREMENT_BY).append(newSequence.getSeqincrement());
            }
            if (!StringUtils.equals(oldSequence.getSeqmax(), newSequence.getSeqmax())) {
                sqlBuilder.append(MAXVALUE).append(newSequence.getSeqmax());
            }
            if (!StringUtils.equals(oldSequence.getSeqmin(), newSequence.getSeqmin())) {
                sqlBuilder.append(MINVALUE).append(newSequence.getSeqmin());
            }
            sqlBuilder.append(SEMICOLON).append(BLANK_LINE);
        }

        if (!StringUtils.equals(oldSequence.getTypname(), newSequence.getTypname())) {
            sqlBuilder.append(ALTER_SEQUENCE).append(getMetaDataName(newSequence.getNspname(), newSequence.getRelname())).append(AS).append(newSequence.getTypname()).append(SEMICOLON).append(BLANK_LINE);
        }
        if (!StringUtils.equals(oldSequence.getRolname(), newSequence.getRolname())) {
            sqlBuilder.append(ALTER_SEQUENCE).append(getMetaDataName(newSequence.getNspname(), newSequence.getRelname())).append(OWNER_TO).append(getMetaDataName(newSequence.getRolname())).append(SEMICOLON).append(BLANK_LINE);
        }
        return sqlBuilder.toString();
    }

    private String getMetaDataName(String... names) {
        return Arrays.stream(names).filter(StringUtils::isNotBlank).map(name -> DOUBLE_SQUOT + name + DOUBLE_SQUOT).collect(Collectors.joining(DOT));
    }

}
