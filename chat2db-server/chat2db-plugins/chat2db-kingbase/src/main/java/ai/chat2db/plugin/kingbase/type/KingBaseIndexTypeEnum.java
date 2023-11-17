package ai.chat2db.plugin.kingbase.type;

import ai.chat2db.spi.enums.EditStatus;
import ai.chat2db.spi.model.IndexType;
import ai.chat2db.spi.model.TableIndex;
import ai.chat2db.spi.model.TableIndexColumn;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public enum KingBaseIndexTypeEnum {

    PRIMARY("Primary", "PRIMARY KEY"),

    FOREIGN("Foreign", "FOREIGN KEY"),

    NORMAL("Normal", "INDEX"),

    UNIQUE("Unique", "UNIQUE"),
    ;

    private String name;
    private String keyword;

    private IndexType indexType;


    KingBaseIndexTypeEnum(String name, String keyword) {
        this.name = name;
        this.keyword = keyword;
        this.indexType =new IndexType(name);
    }

    public static KingBaseIndexTypeEnum getByType(String type) {
        for (KingBaseIndexTypeEnum value : KingBaseIndexTypeEnum.values()) {
            if (value.name.equalsIgnoreCase(type)) {
                return value;
            }
        }
        return null;
    }

    public static List<IndexType> getIndexTypes() {
        return Arrays.asList(KingBaseIndexTypeEnum.values()).stream().map(KingBaseIndexTypeEnum::getIndexType).collect(java.util.stream.Collectors.toList());
    }

    public IndexType getIndexType() {
        return indexType;
    }

    public String getName() {
        return name;
    }

    public String getKeyword() {
        return keyword;
    }

    public String buildIndexScript(TableIndex tableIndex) {
        StringBuilder script = new StringBuilder();
        if (NORMAL.equals(this)) {
            script.append("CREATE").append(" ");
            script.append(buildIndexUnique(tableIndex)).append(" ");
            script.append(buildIndexConcurrently(tableIndex)).append(" ");
            script.append(buildIndexName(tableIndex)).append(" ");
            script.append("ON ").append("\"").append(tableIndex.getTableName()).append("\"").append(" ");
            script.append(buildIndexMethod(tableIndex)).append(" ");
            script.append(buildIndexColumn(tableIndex));
        } else {
            script.append("CONSTRAINT").append(" ");
            script.append(buildIndexName(tableIndex)).append(" ");
            script.append(keyword).append(" ");
            script.append(buildIndexColumn(tableIndex));
            script.append(buildForeignColum(tableIndex));
        }
        return script.toString();
    }

    private String buildForeignColum(TableIndex tableIndex) {
        if (FOREIGN.equals(this)) {
            StringBuilder script = new StringBuilder();
            script.append(" REFERENCES ");
            if (StringUtils.isNotBlank(tableIndex.getForeignSchemaName())) {
                script.append(tableIndex.getForeignSchemaName()).append(".");
            }
            if (StringUtils.isNotBlank(tableIndex.getForeignTableName())) {
                script.append(tableIndex.getForeignTableName()).append(" ");
            }
            if (CollectionUtils.isNotEmpty(tableIndex.getForeignColumnNamelist())) {
                script.append("(");
                for (String column : tableIndex.getForeignColumnNamelist()) {
                    if (StringUtils.isNotBlank(column)) {
                        script.append("\"").append(column).append("\"").append(",");
                    }
                }
                script.deleteCharAt(script.length() - 1);
                script.append(")");
            }
            return script.toString();
        }
        return "";
    }

    private String buildIndexMethod(TableIndex tableIndex) {
        if (StringUtils.isNotBlank(tableIndex.getMethod())) {
            return "USING " + tableIndex.getMethod();
        } else {
            return "";
        }
    }

    private String buildIndexConcurrently(TableIndex tableIndex) {
        if (BooleanUtils.isTrue(tableIndex.getConcurrently())) {
            return "CONCURRENTLY";
        } else {
            return "";
        }
    }

    private String buildIndexUnique(TableIndex tableIndex) {
        if (BooleanUtils.isTrue(tableIndex.getUnique())) {
            return "UNIQUE " + keyword;
        } else {
            return keyword;
        }
    }

    public String buildIndexComment(TableIndex tableIndex) {
        if (StringUtils.isBlank(tableIndex.getComment()) || EditStatus.DELETE.name().equals(tableIndex.getEditStatus())) {
            return "";
        } else if (NORMAL.equals(this)) {
            return StringUtils.join("COMMENT ON INDEX", " ",
                    "\"", tableIndex.getName(), "\" IS '", tableIndex.getComment(), "';");
        } else {
            return StringUtils.join("COMMENT ON CONSTRAINT", " \"", tableIndex.getName(), "\" ON \"", tableIndex.getSchemaName(),
                    "\".\"", tableIndex.getTableName(), "\" IS '", tableIndex.getComment(), "';");
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
        return "\"" + tableIndex.getName() + "\"";
    }

    public String buildModifyIndex(TableIndex tableIndex) {
        boolean isNormal = NORMAL.equals(this);
        if (EditStatus.DELETE.name().equals(tableIndex.getEditStatus())) {
            return buildDropIndex(tableIndex);
        }
        if (EditStatus.MODIFY.name().equals(tableIndex.getEditStatus())) {
            return StringUtils.join(buildDropIndex(tableIndex), isNormal ? ";\n" : ",\n\tADD ", buildIndexScript(tableIndex));
        }
        if (EditStatus.ADD.name().equals(tableIndex.getEditStatus())) {
            return StringUtils.join(isNormal ? "" : "ADD ", buildIndexScript(tableIndex));
        }
        return "";
    }

    private String buildDropIndex(TableIndex tableIndex) {
        if (NORMAL.equals(this)) {
            return StringUtils.join("DROP INDEX \"", tableIndex.getOldName(), "\"");
        }
        return StringUtils.join("DROP CONSTRAINT \"", tableIndex.getOldName(), "\"");
    }
}
