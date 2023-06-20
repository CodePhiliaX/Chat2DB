package com.alibaba.dbhub.server.web.api.controller.rdb.vo;

import com.alibaba.dbhub.server.domain.support.enums.ColumnTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author moji
 * @version TableVO.java, v 0.1 2022年09月16日 17:16 moji Exp $
 * @date 2022/09/16
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnVO {
    /**
     * 名称
     */
    private String name;

    /**
     * 列的类型
     *
     * @see ColumnTypeEnum
     */
    private String dataType;

    /**
     * 列的类型
     * 比如 varchar(100) ,double(10,6)
     */
    private String columnType;

    /**
     * 是否为空
     */
    private Boolean nullable;

    /**
     * 是否主键
     */
    private Boolean primaryKey;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 是否自增
     */
    private Boolean autoIncrement;

    /**
     * 数字精度
     */
    private Integer numericPrecision;

    /**
     * 数字比例
     */
    private Integer numericScale;

    /**
     * 字符串最大长度
     */
    private Integer characterMaximumLength;

    /**
     * 注释
     */
    private String comment;

}
