package ai.chat2db.plugin.db2.type;

import ai.chat2db.spi.enums.EditStatus;
import ai.chat2db.spi.model.IndexType;
import ai.chat2db.spi.model.TableIndex;
import ai.chat2db.spi.model.TableIndexColumn;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public enum DB2IndexTypeEnum {

    PRIMARY_KEY("Primary", "PRIMARY KEY"),

    NORMAL("Normal", "INDEX"),

    UNIQUE("Unique", "UNIQUE INDEX");

   // BITMAP("BITMAP", "BITMAP INDEX");



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

    DB2IndexTypeEnum(String name, String keyword) {
        this.name = name;
        this.keyword = keyword;
        this.indexType = new IndexType(name);
    }


    public static DB2IndexTypeEnum getByType(String type) {
        for (DB2IndexTypeEnum value : DB2IndexTypeEnum.values()) {
            if (value.name.equalsIgnoreCase(type)) {
                return value;
            }
        }
        return null;
    }

    public String buildIndexScript(TableIndex tableIndex) {
        StringBuilder script = new StringBuilder();
        if (PRIMARY_KEY.equals(this)) {
            script.append("ALTER TABLE \"").append(tableIndex.getSchemaName()).append("\".\"").append(tableIndex.getTableName()).append("\" ADD PRIMARY KEY ").append(buildIndexColumn(tableIndex));
        } else {
            if (UNIQUE.equals(this)) {
                script.append("CREATE UNIQUE INDEX ");
            } else {
                script.append("CREATE INDEX ");
            }
            script.append(buildIndexName(tableIndex)).append(" ON \"").append(tableIndex.getSchemaName()).append("\".\"").append(tableIndex.getTableName()).append("\" ").append(buildIndexColumn(tableIndex));
        }

        return script.toString();
    }


    private String buildIndexColumn(TableIndex tableIndex) {
        StringBuilder script = new StringBuilder();
        script.append("(");
        for (TableIndexColumn column : tableIndex.getColumnList()) {
            if (StringUtils.isNotBlank(column.getColumnName())) {
                script.append("\"").append(column.getColumnName()).append("\"");
                if (!StringUtils.isBlank(column.getAscOrDesc()) && !PRIMARY_KEY.equals(this)) {
                    script.append(" ").append(column.getAscOrDesc());
                }
                script.append(",");
            }
        }
        script.deleteCharAt(script.length() - 1);
        script.append(")");
        return script.toString();
    }

    public String buildIndexComment(TableIndex tableIndex) {
        if (StringUtils.isBlank(tableIndex.getComment()) || EditStatus.DELETE.name().equals(tableIndex.getEditStatus())) {
            return "";
        } else if (NORMAL.equals(this) || UNIQUE.equals(this)) {
            return StringUtils.join("COMMENT ON INDEX", " ",
                    "\"", tableIndex.getName(), "\" IS '", tableIndex.getComment(), "';");
        } else {
            return StringUtils.join("COMMENT ON CONSTRAINT", " \"", tableIndex.getName(), "\" ON \"", tableIndex.getSchemaName(),
                    "\".\"", tableIndex.getTableName(), "\" IS '", tableIndex.getComment(), "';");
        }
    }

    private String buildIndexName(TableIndex tableIndex) {
        return "\"" + tableIndex.getSchemaName() + "\"." + "\"" + tableIndex.getName() + "\"";
    }

    public String buildModifyIndex(TableIndex tableIndex) {
        if (EditStatus.DELETE.name().equals(tableIndex.getEditStatus())) {
            return buildDropIndex(tableIndex);
        }
        if (EditStatus.MODIFY.name().equals(tableIndex.getEditStatus())) {
            return StringUtils.join(buildDropIndex(tableIndex), ";\n", buildIndexScript(tableIndex));
        }
        if (EditStatus.ADD.name().equals(tableIndex.getEditStatus())) {
            return StringUtils.join(buildIndexScript(tableIndex));
        }
        return "";
    }

    private String buildDropIndex(TableIndex tableIndex) {
        if (DB2IndexTypeEnum.PRIMARY_KEY.getName().equals(tableIndex.getType())) {
            String tableName = "\"" + tableIndex.getSchemaName() + "\"." + "\"" + tableIndex.getTableName() + "\"";
            return StringUtils.join("ALTER TABLE ",tableName," DROP PRIMARY KEY");
        }
        StringBuilder script = new StringBuilder();
        script.append("DROP INDEX ");
        script.append(buildIndexName(tableIndex));

        return script.toString();
    }

    public static List<IndexType> getIndexTypes() {
        return Arrays.asList(DB2IndexTypeEnum.values()).stream().map(DB2IndexTypeEnum::getIndexType).collect(java.util.stream.Collectors.toList());
    }
}
