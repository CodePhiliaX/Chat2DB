package ai.chat2db.server.tools.base.enums;

import lombok.Getter;

/**
 * @author moji
 * @version ConnectionTypeEnum.java, v 0.1 2022年09月16日 14:59 moji Exp $
 * @date 2022/09/16
 */
@Getter
public enum DataSourceTypeEnum implements BaseEnum<String> {

    /**
     * mysql数据库连接
     */
    MYSQL("mysql数据库连接"),

    /**
     * redis数据库连接
     */
    REDIS("redis数据库连接"),

    ;

    final String description;

    DataSourceTypeEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
