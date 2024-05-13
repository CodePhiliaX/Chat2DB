package ai.chat2db.spi.enums;

import ai.chat2db.server.tools.base.enums.BaseEnum;
import ai.chat2db.server.tools.common.util.EasyEnumUtils;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import lombok.Getter;

/**
 * Sorted enumeration
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum CollationEnum implements BaseEnum<String> {
    /**
     * ASC
     */
    ASC("asc", SQLOrderingSpecification.ASC),

    /**
     * DESC
     */
    DESC("desc", SQLOrderingSpecification.DESC),

    ;

    final String description;

    final SQLOrderingSpecification sqlOrderingSpecification;

    CollationEnum(String description, SQLOrderingSpecification sqlOrderingSpecification) {
        this.description = description;
        this.sqlOrderingSpecification = sqlOrderingSpecification;
    }

    @Override
    public String getCode() {
        return this.name();
    }

    public static boolean equals(String collation1, String collation2) {
        return equals(EasyEnumUtils.getEnum(CollationEnum.class, collation1),
            EasyEnumUtils.getEnum(CollationEnum.class, collation2));
    }

    public static boolean equals(CollationEnum collation1, CollationEnum collation2) {
        // The same returns directly
        if (collation1 == collation2) {
            return true;
        }
        // One of them is in reverse order, which means they are different. The others are the same.
        return !(collation1 == CollationEnum.DESC || collation2 == CollationEnum.DESC);
    }
}
