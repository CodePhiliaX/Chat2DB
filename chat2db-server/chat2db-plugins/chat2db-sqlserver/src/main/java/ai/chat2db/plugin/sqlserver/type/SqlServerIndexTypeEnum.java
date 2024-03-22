package ai.chat2db.plugin.sqlserver.type;

import ai.chat2db.spi.enums.EditStatus;
import ai.chat2db.spi.model.IndexType;
import ai.chat2db.spi.model.TableIndex;
import ai.chat2db.spi.model.TableIndexColumn;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public enum SqlServerIndexTypeEnum {

    PRIMARY_KEY("Primary", "PRIMARY KEY"),

//    NORMAL("Normal", "INDEX"),
//
//    UNIQUE("Unique", "UNIQUE INDEX"),


    UNIQUE_CLUSTERED("UNIQUE CLUSTERED", "UNIQUE CLUSTERED INDEX"),

    CLUSTERED("CLUSTERED", "CLUSTERED INDEX"),


    NONCLUSTERED("NONCLUSTERED", "NONCLUSTERED INDEX"),

    UNIQUE_NONCLUSTERED("UNIQUE NONCLUSTERED", "UNIQUE NONCLUSTERED INDEX"),

    SPATIAL("SPATIAL", "SPATIAL INDEX"),

    XML("XML", "XML INDEX");

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

    SqlServerIndexTypeEnum(String name, String keyword) {
        this.name = name;
        this.keyword = keyword;
        this.indexType = new IndexType(name);
    }


    public static SqlServerIndexTypeEnum getByType(String type) {
        for (SqlServerIndexTypeEnum value : SqlServerIndexTypeEnum.values()) {
            if (value.name.equalsIgnoreCase(type)) {
                return value;
            }
        }
        return null;
    }

    //ALTER TABLE [dbo].[Employees] ADD CONSTRAINT [PK__Employee__7AD04FF164ABF7C7] PRIMARY KEY CLUSTERED ([FirstName])

    public String buildIndexScript(TableIndex tableIndex) {
        StringBuilder script = new StringBuilder();
        if (PRIMARY_KEY.equals(this)) {
            script.append("ALTER TABLE [").append(tableIndex.getSchemaName()).append("].[").append(tableIndex.getTableName()).append("] ADD CONSTRAINT ").append(buildIndexName(tableIndex)).append(" ").append(keyword).append(" ").append(buildIndexColumn(tableIndex));
        } else {
            script.append("CREATE ").append(keyword).append(" ");
            script.append(buildIndexName(tableIndex)).append("\n ON [").append(tableIndex.getSchemaName()).append("].[").append(tableIndex.getTableName()).append("] ").append(buildIndexColumn(tableIndex));
        }
        script.append("\ngo");
        return script.toString();
    }


    private String buildIndexColumn(TableIndex tableIndex) {
        StringBuilder script = new StringBuilder();
        script.append("(");
        for (TableIndexColumn column : tableIndex.getColumnList()) {
            if (StringUtils.isNotBlank(column.getColumnName())) {
                script.append("[").append(column.getColumnName()).append("]");
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

    private String buildIndexName(TableIndex tableIndex) {
        return "[" + tableIndex.getName() + "]";
    }

    public String buildModifyIndex(TableIndex tableIndex) {
        if (EditStatus.DELETE.name().equals(tableIndex.getEditStatus())) {
            return buildDropIndex(tableIndex);
        }
        if (EditStatus.MODIFY.name().equals(tableIndex.getEditStatus())) {
            return StringUtils.join(buildDropIndex(tableIndex), "\n", buildIndexScript(tableIndex));
        }
        if (EditStatus.ADD.name().equals(tableIndex.getEditStatus())) {
            return StringUtils.join(buildIndexScript(tableIndex));
        }
        return "";
    }

    private String buildDropIndex(TableIndex tableIndex) {
        if (SqlServerIndexTypeEnum.PRIMARY_KEY.getName().equals(tableIndex.getType())) {
            return StringUtils.join("ALTER TABLE [", tableIndex.getSchemaName(), "].[", tableIndex.getTableName(), "] DROP CONSTRAINT ", buildIndexName(tableIndex),"\ngo");
        }
        StringBuilder script = new StringBuilder();
        script.append("DROP INDEX ");
        script.append(buildIndexName(tableIndex));
        script.append(" ON [").append(tableIndex.getSchemaName()).append("].[").append(tableIndex.getTableName()).append("] \ngo");

        return script.toString();
    }

    public static List<IndexType> getIndexTypes() {
        return Arrays.asList(SqlServerIndexTypeEnum.values()).stream().map(SqlServerIndexTypeEnum::getIndexType).collect(java.util.stream.Collectors.toList());
    }}
