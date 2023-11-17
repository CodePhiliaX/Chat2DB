package ai.chat2db.plugin.sqlite.type;

import ai.chat2db.spi.enums.EditStatus;
import ai.chat2db.spi.model.Collation;
import ai.chat2db.spi.model.IndexType;
import ai.chat2db.spi.model.TableIndex;
import ai.chat2db.spi.model.TableIndexColumn;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public enum SqliteIndexTypeEnum {

    PRIMARY_KEY("Primary", "PRIMARY KEY"),

    NORMAL("Normal", "INDEX"),

    UNIQUE("Unique", "UNIQUE INDEX");


    public IndexType getIndexType() {
        return indexType;
    }

    public void setIndexType(IndexType indexType) {
        this.indexType = indexType;
    }

    private IndexType indexType;

    public String getName() {
        return name;
    }

    private String name;


    public String getKeyword() {
        return keyword;
    }

    private String keyword;

    SqliteIndexTypeEnum(String name, String keyword) {
        this.name = name;
        this.keyword = keyword;
        this.indexType = new IndexType(name);
    }


    public static SqliteIndexTypeEnum getByType(String type) {
        for (SqliteIndexTypeEnum value : SqliteIndexTypeEnum.values()) {
            if (value.name.equalsIgnoreCase(type)) {
                return value;
            }
        }
        return null;
    }

    public String buildIndexScript(TableIndex tableIndex) {
        if (this.equals(PRIMARY_KEY)) {
            return buildPrimaryKeyScript(tableIndex);
        } else {
            StringBuilder script = new StringBuilder();

            script.append(keyword).append(" ");

            script.append(buildIndexName(tableIndex)).append(" ON ").append(tableIndex.getTableName()).append(" ");

            script.append(buildIndexColumn(tableIndex)).append(" ");
            return script.toString();
        }


    }

    private String buildPrimaryKeyScript(TableIndex tableIndex) {
        StringBuilder script = new StringBuilder();
        script.append("CONSTRAINT ").append(buildIndexName(tableIndex)).append(" ").append(keyword).append(" ").append(buildIndexColumn(tableIndex));
        return script.toString();
    }

    private String buildIndexComment(TableIndex tableIndex) {
        if (StringUtils.isBlank(tableIndex.getComment())) {
            return "";
        } else {
            return StringUtils.join("COMMENT '", tableIndex.getComment(), "'");
        }

    }

    private String buildIndexColumn(TableIndex tableIndex) {
        StringBuilder script = new StringBuilder();
        script.append("(");
        for (TableIndexColumn column : tableIndex.getColumnList()) {
            if (StringUtils.isNotBlank(column.getColumnName())) {
                script.append("\"").append(column.getColumnName()).append("\"").append(",");
            }
        }
        script.deleteCharAt(script.length() - 1);
        script.append(")");
        return script.toString();
    }

    private String buildIndexName(TableIndex tableIndex) {
        if (this.equals(PRIMARY_KEY)) {
            return tableIndex.getTableName()+"_pk";
        } else {
            return "\"" + tableIndex.getName() + "\"";
        }
    }

    public String buildModifyIndex(TableIndex tableIndex) {
        if (EditStatus.DELETE.name().equals(tableIndex.getEditStatus())) {
            return buildDropIndex(tableIndex);
        }
        if (EditStatus.MODIFY.name().equals(tableIndex.getEditStatus())) {
            return StringUtils.join(buildDropIndex(tableIndex), ",\n", "ADD ", buildIndexScript(tableIndex));
        }
        if (EditStatus.ADD.name().equals(tableIndex.getEditStatus())) {
            return StringUtils.join("CREATE ", buildIndexScript(tableIndex));
        }
        return "";
    }

    private String buildDropIndex(TableIndex tableIndex) {
        if (SqliteIndexTypeEnum.PRIMARY_KEY.getName().equals(tableIndex.getType())) {
            return StringUtils.join("DROP PRIMARY KEY");
        }
        return StringUtils.join("DROP INDEX \"", tableIndex.getOldName(), "\"");
    }

    public static List<IndexType> getIndexTypes() {
        return Arrays.asList(SqliteIndexTypeEnum.values()).stream().map(SqliteIndexTypeEnum::getIndexType).collect(java.util.stream.Collectors.toList());
    }
}
