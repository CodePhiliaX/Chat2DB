package ai.chat2db.plugin.clickhouse.type;

import ai.chat2db.spi.enums.EditStatus;
import ai.chat2db.spi.model.IndexType;
import ai.chat2db.spi.model.TableIndex;
import ai.chat2db.spi.model.TableIndexColumn;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public enum ClickHouseIndexTypeEnum {

    PRIMARY("Primary", "PRIMARY KEY"),
    MINMAX("MINMAX", "INDEX"),
    SET("SET", "INDEX"),
    BLOOM_FILTER("BLOOM_FILTER", "INDEX"),
    TOKENBF_V1("TOKENBF_V1", "INDEX"),
    NGRAMBF_V1("NGRAMBF_V1", "INDEX"),
    INVERTED("INVERTED", "INDEX"),
    HYPOTHESIS("HYPOTHESIS", "INDEX"),
    ANNOY("ANNOY", "INDEX"),
    USEARCH("USEARCH", "INDEX"),

    ;

    private String name;
    private String keyword;
    private IndexType indexType;

    ClickHouseIndexTypeEnum(String name, String keyword) {
        this.name = name;
        this.keyword = keyword;
        this.indexType = new IndexType(name);
    }

    public static ClickHouseIndexTypeEnum getByType(String type) {
        for (ClickHouseIndexTypeEnum value : ClickHouseIndexTypeEnum.values()) {
            if (value.name.equalsIgnoreCase(type)) {
                return value;
            }
        }
        return null;
    }

    public static List<IndexType> getIndexTypes() {
        return Arrays.asList(ClickHouseIndexTypeEnum.values()).stream().map(ClickHouseIndexTypeEnum::getIndexType).collect(java.util.stream.Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public String getKeyword() {
        return keyword;
    }

    public IndexType getIndexType() {
        return indexType;
    }

    public void setIndexType(IndexType indexType) {
        this.indexType = indexType;
    }

    public String buildIndexScript(TableIndex tableIndex) {
        StringBuilder script = new StringBuilder();

        script.append(keyword).append(" ");
        script.append(buildIndexName(tableIndex)).append(" ");
        script.append(buildIndexColumn(tableIndex)).append(" ");
        script.append(buildIndexType(tableIndex)).append(" ");
        return script.toString();
    }

    private String buildIndexType(TableIndex tableIndex) {
        if (this.equals(PRIMARY)) {
            return "";
        } else {
            return "TYPE " + name ;
        }
    }

    private String buildIndexColumn(TableIndex tableIndex) {
        StringBuilder script = new StringBuilder();
        script.append("(");
        for (TableIndexColumn column : tableIndex.getColumnList()) {
            if (StringUtils.isNotBlank(column.getColumnName())) {
                script.append("`").append(column.getColumnName()).append("`");
                script.append(",");
            }
        }
        script.deleteCharAt(script.length() - 1);
        script.append(")");
        return script.toString();
    }

    private String buildIndexName(TableIndex tableIndex) {
        if (this.equals(PRIMARY)) {
            return "";
        } else {
            return "`" + tableIndex.getName() + "`";
        }
    }

    public String buildModifyIndex(TableIndex tableIndex) {
        if (this.equals(PRIMARY)) {
            return "";
        }
        if (EditStatus.DELETE.name().equals(tableIndex.getEditStatus())) {
            return StringUtils.join("DROP INDEX `", tableIndex.getOldName(), "`");
        }
        if (EditStatus.MODIFY.name().equals(tableIndex.getEditStatus())) {
            return StringUtils.join("DROP INDEX `", tableIndex.getOldName(),
                    "`,\n ADD ", buildIndexScript(tableIndex));
        }
        if (EditStatus.ADD.name().equals(tableIndex.getEditStatus())) {
            return StringUtils.join("ADD ", buildIndexScript(tableIndex));
        }
        return "";
    }

}
