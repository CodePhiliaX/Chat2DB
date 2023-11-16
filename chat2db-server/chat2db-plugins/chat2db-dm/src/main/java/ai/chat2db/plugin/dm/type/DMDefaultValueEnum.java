package ai.chat2db.plugin.dm.type;

import ai.chat2db.spi.model.DefaultValue;

import java.util.Arrays;
import java.util.List;

public enum DMDefaultValueEnum {
    EMPTY_STRING("EMPTY_STRING"),
    NULL("NULL"),
    ;
    private DefaultValue defaultValue;

    DMDefaultValueEnum(String defaultValue) {
        this.defaultValue = new DefaultValue(defaultValue);
    }


    public DefaultValue getDefaultValue() {
        return defaultValue;
    }

    public static List<DefaultValue> getDefaultValues() {
        return Arrays.stream(DMDefaultValueEnum.values()).map(DMDefaultValueEnum::getDefaultValue).collect(java.util.stream.Collectors.toList());
    }

}
