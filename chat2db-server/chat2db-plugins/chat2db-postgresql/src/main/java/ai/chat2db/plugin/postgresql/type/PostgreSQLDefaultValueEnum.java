package ai.chat2db.plugin.postgresql.type;

import ai.chat2db.spi.model.DefaultValue;

import java.util.Arrays;
import java.util.List;

public enum PostgreSQLDefaultValueEnum {
    EMPTY_STRING("EMPTY_STRING"),
    NULL("NULL"),
    ;
    private DefaultValue defaultValue;

    PostgreSQLDefaultValueEnum(String defaultValue) {
        this.defaultValue = new DefaultValue(defaultValue);
    }


    public DefaultValue getDefaultValue() {
        return defaultValue;
    }

    public static List<DefaultValue> getDefaultValues() {
        return Arrays.stream(PostgreSQLDefaultValueEnum.values()).map(PostgreSQLDefaultValueEnum::getDefaultValue).collect(java.util.stream.Collectors.toList());
    }

}
