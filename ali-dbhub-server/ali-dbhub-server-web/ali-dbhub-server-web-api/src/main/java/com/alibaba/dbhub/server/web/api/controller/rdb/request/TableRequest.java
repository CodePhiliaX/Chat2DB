package com.alibaba.dbhub.server.web.api.controller.rdb.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 修改表sql请求
 *
 * @author 是仪
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TableRequest {
    /**
     * 表名称
     */
    private String name;

    /**
     * 表描述
     */
    private String comment;

    /**
     * 列
     */
    private List<ColumnRequest> columnList;

    /**
     * 索引
     */
    private List<IndexRequest> indexList;
}
