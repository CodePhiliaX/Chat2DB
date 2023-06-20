package com.alibaba.dbhub.server.web.api.controller.rdb.vo;

import java.util.List;

import com.alibaba.dbhub.server.domain.support.enums.IndexTypeEnum;

import lombok.Data;

/**
 * @author moji
 * @version IndexVO.java, v 0.1 2022年09月16日 17:47 moji Exp $
 * @date 2022/09/16
 */
@Data
public class IndexVO {

    /**
     * 包含列
     */
    private String columns;

    /**
     * 索引名称
     */
    private String name;

    /**
     * 所以类型
     *
     * @see IndexTypeEnum
     */
    private String type;

    /**
     * 注释
     */
    private String comment;

    /**
     * 索引包含的列
     */
    private List<ColumnVO> columnList;
}
