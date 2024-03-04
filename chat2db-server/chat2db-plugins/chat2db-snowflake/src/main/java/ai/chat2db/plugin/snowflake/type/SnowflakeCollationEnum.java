package ai.chat2db.plugin.snowflake.type;

import ai.chat2db.spi.model.Collation;

import java.util.Arrays;
import java.util.List;

public enum SnowflakeCollationEnum {

    BINARY("BINARY"),

    CASE_INSENSITIVE("CASE_INSENSITIVE"),

    CASE_SENSITIVE("CASE_SENSITIVE"),
    ;
    private Collation collation;

    SnowflakeCollationEnum(String collationName) {
        this.collation = new Collation(collationName);
    }

    public Collation getCollation() {
        return collation;
    }


    public static List<Collation> getCollations() {
        return Arrays.asList(SnowflakeCollationEnum.values()).stream().map(SnowflakeCollationEnum::getCollation).collect(java.util.stream.Collectors.toList());
    }

}
