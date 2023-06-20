package com.alibaba.dbhub.server.domain.support.enums;

import com.alibaba.dbhub.server.tools.base.enums.BaseEnum;
import lombok.Getter;

/**
 * 驱动类枚举
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum DataTypeEnum implements BaseEnum<String> {
    /**
     * 布尔值
     */
    BOOLEAN("布尔值"),

    /**
     * 数字
     */
    NUMERIC("数字"),

    /**
     * 字符串
     */
    STRING("字符串"),

    /**
     * 日期
     */
    DATETIME("日期"),

    /**
     * 二进制
     */
    BINARY("空数据"),

    /**
     * 内容
     */
    CONTENT("内容"),

    /**
     * 结构
     */
    STRUCT("结构"),

    /**
     * 文档
     */
    DOCUMENT("文档"),

    /**
     * 数组
     */
    ARRAY("数组"),


    /**
     * 对象
     */
    OBJECT("对象"),


    /**
     * 引用
     */
    REFERENCE("引用"),


    /**
     * 行号
     */
    ROWID("行号"),


    /**
     * 任何
     */
    ANY("任何"),


    /**
     * 未知
     */
    UNKNOWN("未知"),
    ;

    final String description;

    DataTypeEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
