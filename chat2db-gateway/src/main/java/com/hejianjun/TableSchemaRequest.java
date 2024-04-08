package com.hejianjun;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

/**
 * 表结构请求
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TableSchemaRequest {

    // 数据源ID
    @NotNull
    private Long dataSourceId;
    // 数据库名称
    @NotNull
    private String databaseName;
    // API密钥
    private String apiKey;
    // 数据源模式
    private String dataSourceSchema;
    // 模式向量
    @NotNull
    private List<List<BigDecimal>> schemaVector;
    // 模式列表
    @NotNull
    private List<String> schemaList;
    // 插入前删除
    private Boolean deleteBeforeInsert = false;
}
