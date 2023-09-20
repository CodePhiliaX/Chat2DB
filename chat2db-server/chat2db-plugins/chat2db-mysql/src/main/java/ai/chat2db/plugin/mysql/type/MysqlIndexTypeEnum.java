package ai.chat2db.plugin.mysql.type;

import ai.chat2db.spi.enums.EditStatus;
import ai.chat2db.spi.model.TableIndex;
import ai.chat2db.spi.model.TableIndexColumn;
import org.apache.commons.lang3.StringUtils;

public enum MysqlIndexTypeEnum {

    PRIMARY_KEY("Primary", "PRIMARY KEY"),

    NORMAL("Normal", "KEY"),

    UNIQUE("Unique", "UNIQUE KEY"),

    FULLTEXT("Fulltext", "FULLTEXT KEY"),

    SPATIAL("Spatial", "SPATIAL KEY");

    public String getName() {
        return name;
    }

    private String name;


    public String getKeyword() {
        return keyword;
    }

    private String keyword;

    MysqlIndexTypeEnum(String name, String keyword) {
        this.name = name;
        this.keyword = keyword;
    }


    public static MysqlIndexTypeEnum getByType(String type) {
        for (MysqlIndexTypeEnum value : MysqlIndexTypeEnum.values()) {
            if (value.name.equals(type)) {
                return value;
            }
        }
        return null;
    }

    public String buildIndexScript(TableIndex tableIndex) {
        StringBuilder script = new StringBuilder();

        script.append(keyword).append(" ");

        script.append(buildIndexName(tableIndex)).append(" ");

        script.append(buildIndexColumn(tableIndex)).append(" ");

        script.append(buildIndexComment(tableIndex)).append(" ");

        return script.toString();
    }

    private String buildIndexComment(TableIndex tableIndex) {
        if(StringUtils.isBlank(tableIndex.getComment())){
            return "";
        }else {
            return StringUtils.join("COMMENT '",tableIndex.getComment(),"'");
        }

    }

    private String buildIndexColumn(TableIndex tableIndex) {
        StringBuilder script = new StringBuilder();
        script.append("(");
        for (TableIndexColumn column : tableIndex.getColumnList()) {
            script.append("`").append(column.getColumnName()).append("`").append(",");
        }
        script.deleteCharAt(script.length() - 1);
        script.append(")");
        return script.toString();
    }

    private String buildIndexName(TableIndex tableIndex) {
        if(this.equals(PRIMARY_KEY)){
            return "";
        }else {
            return "`"+tableIndex.getName()+"`";
        }
    }

    public String buildModifyIndex(TableIndex tableIndex) {
        if (EditStatus.DELETE.name().equals(tableIndex.getEditStatus())) {
            return buildDropIndex(tableIndex);
        }
        if (EditStatus.ADD.name().equals(tableIndex.getEditStatus())) {
            return StringUtils.join(buildDropIndex(tableIndex),",\n", "ADD ", buildIndexScript(tableIndex));
        }
        if (EditStatus.MODIFY.name().equals(tableIndex.getEditStatus())) {
            return StringUtils.join("MODIFY ", buildIndexScript(tableIndex));
        }
        return "";
    }

    private String buildDropIndex(TableIndex tableIndex) {
        if (MysqlIndexTypeEnum.PRIMARY_KEY.getName().equals(tableIndex.getType())) {
            return StringUtils.join("DROP PRIMARY KEY");
        }
        return StringUtils.join("DROP KEY `", tableIndex.getName());
    }
}
