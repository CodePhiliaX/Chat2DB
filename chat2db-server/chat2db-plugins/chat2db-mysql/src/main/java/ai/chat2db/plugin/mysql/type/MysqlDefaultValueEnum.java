package ai.chat2db.plugin.mysql.type;

import ai.chat2db.spi.model.DefaultValue;

import java.util.Arrays;
import java.util.List;

public enum MysqlDefaultValueEnum {

    EMPTY_STRING("EMPTY_STRING"),
    NULL("NULL"),
    CURRENT_TIMESTAMP("CURRENT_TIMESTAMP"),
    ;
    private DefaultValue defaultValue;

    MysqlDefaultValueEnum(String defaultValue) {
        this.defaultValue = new DefaultValue(defaultValue);
    }


    public DefaultValue getDefaultValue() {
        return defaultValue;
    }

    public static List<DefaultValue> getDefaultValues() {
        return Arrays.stream(MysqlDefaultValueEnum.values()).map(MysqlDefaultValueEnum::getDefaultValue).collect(java.util.stream.Collectors.toList());
    }

}
