package ai.chat2db.plugin.oceanbase.type;

import ai.chat2db.spi.model.Charset;

import java.util.Arrays;
import java.util.List;

public enum OceanBaseCharsetEnum {

    BINARY("binary", "binary"),

    GBK("gbk", "gbk_chinese_ci"),

    GB18030("gb18030", "gb18030_chinese_ci"),

    UTF16("utf16", "utf16_general_ci"),

    UTF8MB4("utf8mb4", "utf8mb4_general_ci"),
    ;

    private Charset charset;

    OceanBaseCharsetEnum(String charsetName, String defaultCollationName) {
        this.charset = new Charset(charsetName, defaultCollationName);
    }


    public Charset getCharset() {
        return charset;
    }

    public static List<Charset> getCharsets() {
        return Arrays.stream(OceanBaseCharsetEnum.values()).map(OceanBaseCharsetEnum::getCharset).collect(java.util.stream.Collectors.toList());
    }

}
