package ai.chat2db.spi.model;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 索引信息
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TableIndex {

    private String oldName;

    /**
     * 索引名称
     */
    private String name;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 索引类型
     *
     * @see IndexTypeEnum
     */
    private String type;

    /**
     * 是否唯一
     */
    private Boolean unique;

    /**
     * 注释
     */
    private String comment;

    /**
     * 索引所属schema
     */
    private String schemaName;

    /**
     * 数据库名
     */
    private String databaseName;

    /**
     * 索引包含的列
     */
    private List<TableIndexColumn> columnList;


    private String editStatus;

    /**
     * 是否并发
     */
    private Boolean concurrently;

    /**
     * 索引方法
     */
    private String method;


    /**
     * 外键指向schema
     */
    private String foreignSchemaName;

    /**
     * 外键指向表名
     */
    private String foreignTableName;

    /**
     * 外键指向的列名
     */
    private List<String> foreignColumnNamelist;

}
