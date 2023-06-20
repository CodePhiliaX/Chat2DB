package com.alibaba.dbhub.server.domain.api.enums;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.dbhub.server.domain.support.dialect.MetaSchema;
import com.alibaba.dbhub.server.tools.base.enums.BaseEnum;

import lombok.Getter;

/**
 * AI SQL选择的AI模型类型
 *
 * @author moji
 */
@Getter
public enum AiSqlSourceEnum implements BaseEnum<String> {
    /**
     * 使用OPENAI接口
     */
    OPENAI( "使用OPENAI接口"),

    /**
     * 自定义RESTAI接口
     */
    RESTAI("自定义RESTAI接口"),

    ;

    final String description;

    private static Map<AiSqlSourceEnum, MetaSchema> META_SCHEMA_MAP = new HashMap<>();

    AiSqlSourceEnum(String description) {
        this.description = description;
    }

    /**
     * 通过名称获取枚举
     *
     * @param name
     * @return
     */
    public static AiSqlSourceEnum getByName(String name) {
        for (AiSqlSourceEnum dbTypeEnum : AiSqlSourceEnum.values()) {
            if (dbTypeEnum.name().equals(name)) {
                return dbTypeEnum;
            }
        }
        return null;
    }

    @Override
    public String getCode() {
        return this.name();
    }

}
