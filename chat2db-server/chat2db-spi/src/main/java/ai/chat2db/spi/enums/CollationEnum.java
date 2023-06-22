package ai.chat2db.spi.enums;

import ai.chat2db.server.tools.base.enums.BaseEnum;
import ai.chat2db.server.tools.common.util.EasyEnumUtils;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import lombok.Getter;

/**
 * 排序枚举
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
        // 想同直接返回
        if (collation1 == collation2) {
            return true;
        }
        // 有一个是倒序 就是不相同 ，其他都是相同
        return !(collation1 == CollationEnum.DESC || collation2 == CollationEnum.DESC);
    }
}
