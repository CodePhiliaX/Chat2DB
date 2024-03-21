package ai.chat2db.plugin.oceanbase.type;

import ai.chat2db.spi.model.DefaultValue;

import java.util.Arrays;
import java.util.List;

public enum OceanBaseDefaultValueEnum {

    EMPTY_STRING("EMPTY_STRING"),
    NULL("NULL"),
    CURRENT_TIMESTAMP("CURRENT_TIMESTAMP"),
    ;
    private DefaultValue defaultValue;

    OceanBaseDefaultValueEnum(String defaultValue) {
        this.defaultValue = new DefaultValue(defaultValue);
    }


    public DefaultValue getDefaultValue() {
        return defaultValue;
    }

    public static List<DefaultValue> getDefaultValues() {
        return Arrays.stream(OceanBaseDefaultValueEnum.values()).map(OceanBaseDefaultValueEnum::getDefaultValue).collect(java.util.stream.Collectors.toList());
    }

}
