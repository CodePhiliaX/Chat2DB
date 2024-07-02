package ai.chat2db.plugin.sundb.type;

import ai.chat2db.spi.model.DefaultValue;

import java.util.Arrays;
import java.util.List;

public enum SUNDBDefaultValueEnum {
    EMPTY_STRING("EMPTY_STRING"),
    NULL("NULL"),
    ;
    private DefaultValue defaultValue;

    SUNDBDefaultValueEnum(String defaultValue) {
        this.defaultValue = new DefaultValue(defaultValue);
    }


    public DefaultValue getDefaultValue() {
        return defaultValue;
    }

    public static List<DefaultValue> getDefaultValues() {
        return Arrays.stream(SUNDBDefaultValueEnum.values()).map(SUNDBDefaultValueEnum::getDefaultValue).collect(java.util.stream.Collectors.toList());
    }

}
