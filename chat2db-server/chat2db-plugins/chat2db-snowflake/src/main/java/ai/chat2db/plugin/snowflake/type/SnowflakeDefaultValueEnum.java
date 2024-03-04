package ai.chat2db.plugin.snowflake.type;

import ai.chat2db.spi.model.DefaultValue;

import java.util.Arrays;
import java.util.List;

public enum SnowflakeDefaultValueEnum {


    NULL("NULL"),
    CURRENT_DATE("CURRENT_DATE"),
    CURRENT_TIMESTAMP("CURRENT_TIMESTAMP"),
    ;
    private DefaultValue defaultValue;

    SnowflakeDefaultValueEnum(String defaultValue) {
        this.defaultValue = new DefaultValue(defaultValue);
    }


    public DefaultValue getDefaultValue() {
        return defaultValue;
    }

    public static List<DefaultValue> getDefaultValues() {
        return Arrays.stream(SnowflakeDefaultValueEnum.values()).map(SnowflakeDefaultValueEnum::getDefaultValue).collect(java.util.stream.Collectors.toList());
    }

}
