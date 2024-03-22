package ai.chat2db.server.web.api.controller.rdb.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 列信息
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IndexColumnVO {

    /**
     * 索引名称
     */
    private String indexName;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 索引类型
     *
     * @see
     */
    private String type;

    /**
     * 注释
     */
    private String comment;

    /**
     * 列名
     */
    private String columnName;

    /**
     * 顺序
     */
    private Short ordinalPosition;

    /**
     * 排序
     *
     */
    private String collation;


    /**
     * 索引所属schema
     */
    private String schemaName;

    /**
     * 数据库名
     */
    private String databaseName;

    /**
     * 是否唯一
     */
    private Boolean nonUnique;

    /**
     *  index catalog (may be null); null when TYPE is tableIndexStatistic
     */
    private String indexQualifier;

    /**
     * ASC_OR_DESC String => column sort sequence, "A" => ascending, "D" => descending, may be null if sort sequence is not supported; null when TYPE is tableIndexStatistic
     */
    private String ascOrDesc;

    /**
     * CARDINALITY long => When TYPE is tableIndexStatistic, then this is the number of rows in the table; otherwise, it is the number of unique values in the index.
     */
    private Long cardinality;

    /**
     * When TYPE is tableIndexStatistic then this is the number of pages used for the table, otherwise it is the number of pages used for the current index.
     */
    private Long pages;

    /**
     * Filter condition, if any. (may be null)
     */
    private String filterCondition;
}

