package ai.chat2db.plugin.sqlserver.type;

import ai.chat2db.spi.model.DefaultValue;

import java.util.Arrays;
import java.util.List;

public enum SqlServerDefaultValueEnum {
    EMPTY_STRING("EMPTY_STRING"),
    NULL("NULL"),
    ;
    private DefaultValue defaultValue;

    SqlServerDefaultValueEnum(String defaultValue) {
        this.defaultValue = new DefaultValue(defaultValue);
    }


    public DefaultValue getDefaultValue() {
        return defaultValue;
    }

    public static List<DefaultValue> getDefaultValues() {
        return Arrays.stream(SqlServerDefaultValueEnum.values()).map(SqlServerDefaultValueEnum::getDefaultValue).collect(java.util.stream.Collectors.toList());
    }

}
