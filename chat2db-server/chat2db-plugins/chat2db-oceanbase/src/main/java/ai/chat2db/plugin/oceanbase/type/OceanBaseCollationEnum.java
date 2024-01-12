package ai.chat2db.plugin.oceanbase.type;

import ai.chat2db.spi.model.Collation;

import java.util.Arrays;
import java.util.List;

public enum OceanBaseCollationEnum {

    UTF8MB4_GENERAL_CI("utf8mb4_general_ci"),

    UTF8MB4_BIN("utf8mb4_bin"),

    BINARY("binary"),

    GBK_CHINESE_CI("gbk_chinese_ci"),

    GBK_BIN("gbk_bin"),

    UTF16_GENERAL_CI("utf16_general_ci"),

    UTF16_BIN("utf16_bin"),

    GB18030_CHINESE_CI("gb18030_chinese_ci"),

    GB18030_BIN("gb18030_bin"),
    ;

    private Collation collation;

    OceanBaseCollationEnum(String collationName) {
        this.collation = new Collation(collationName);
    }

    public Collation getCollation() {
        return collation;
    }


    public static List<Collation> getCollations() {
        return Arrays.asList(OceanBaseCollationEnum.values()).stream().map(OceanBaseCollationEnum::getCollation).collect(java.util.stream.Collectors.toList());
    }

}
