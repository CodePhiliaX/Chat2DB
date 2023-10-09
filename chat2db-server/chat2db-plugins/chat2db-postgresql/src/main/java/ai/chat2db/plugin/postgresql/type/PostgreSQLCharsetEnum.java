package ai.chat2db.plugin.postgresql.type;

import ai.chat2db.spi.model.Charset;

import java.util.Arrays;
import java.util.List;

public enum PostgreSQLCharsetEnum {
    BIG5("BIG5",null),
    EUC_CN("EUC_CN",null),
    EUC_JP("EUC_JP",null),
    EUC_JIS_2004("EUC_JIS_2004",null),
    EUC_KR("EUC_KR",null),
    EUC_TW("EUC_TW",null),
    GB18030("GB18030",null),
    GBK("GBK",null),
    ISO_8859_5("ISO_8859_5",null),
    ISO_8859_6("ISO_8859_6",null),
    ISO_8859_7("ISO_8859_7",null),
    ISO_8859_8("ISO_8859_8",null),
    JOHAB("JOHAB",null),
    KOI8R("KOI8R",null),
    KOI8U("KOI8U",null),
    LATIN1("LATIN1",null),
    LATIN2("LATIN2",null),
    LATIN3("LATIN3",null),
    LATIN4("LATIN4",null),
    LATIN5("LATIN5",null),
    LATIN6("LATIN6",null),
    LATIN7("LATIN7",null),
    LATIN8("LATIN8",null),
    LATIN9("LATIN9",null),
    LATIN10("LATIN10",null),
    MULE_INTERNAL("MULE_INTERNAL",null),
    SJIS("SJIS",null),
    SHIFT_JIS_2004("SHIFT_JIS_2004",null),
    SQL_ASCII("SQL_ASCII",null),
    UHC("UHC",null),
    UTF8("UTF8",null),
    WIN866("WIN866",null),
    WIN874("WIN874",null),
    WIN1250("WIN1250",null),
    WIN1251("WIN1251",null),
    WIN1252("WIN1252",null),
    WIN1253("WIN1253",null),
    WIN1254("WIN1254",null),
    WIN1255("WIN1255",null),
    WIN1256("WIN1256",null),
    WIN1257("WIN1257",null),
    WIN1258("WIN1258",null),

    ;

    private Charset charset;

    PostgreSQLCharsetEnum(String charsetName, String defaultCollationName) {
        this.charset = new Charset(charsetName, defaultCollationName);
    }

    public static List<Charset> getCharsets() {
        return Arrays.stream(PostgreSQLCharsetEnum.values()).map(PostgreSQLCharsetEnum::getCharset).collect(java.util.stream.Collectors.toList());
    }

    public Charset getCharset() {
        return charset;
    }

}
