package ai.chat2db.spi.model;


import com.fasterxml.jackson.annotation.JsonAlias;
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
public class TableIndexColumn {

    /**
     * 索引名称
     */
    @JsonAlias({"INDEX_NAME"})
    private String indexName;

    /**
     * 表名
     */
    @JsonAlias ({"TABLE_NAME"})
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
    @JsonAlias({"COLUMN_NAME"})
    private String columnName;

    /**
     * 顺序
     */
    @JsonAlias({"ORDINAL_POSITION"})
    private Short ordinalPosition;

    /**
     * 排序
     *
     */
    private String collation;


    /**
     * 索引所属schema
     */
    @JsonAlias({"TABLE_SCHEM"})
    private String schemaName;

    /**
     * 数据库名
     */
    @JsonAlias({"TABLE_CAT"})
    private String databaseName;

    /**
     * 是否唯一
     */
    @JsonAlias({"NON_UNIQUE"})
    private Boolean nonUnique;

    /**
     *  index catalog (may be null); null when TYPE is tableIndexStatistic
     */
    @JsonAlias({"INDEX_QUALIFIER"})
    private String indexQualifier;

    /**
     * ASC_OR_DESC String => column sort sequence, "A" => ascending, "D" => descending, may be null if sort sequence is not supported; null when TYPE is tableIndexStatistic
     */
    @JsonAlias({"ASC_OR_DESC"})
    private String ascOrDesc;

    /**
     * CARDINALITY long => When TYPE is tableIndexStatistic, then this is the number of rows in the table; otherwise, it is the number of unique values in the index.
     */
    @JsonAlias({"CARDINALITY"})
    private Long cardinality;

    /**
     * When TYPE is tableIndexStatistic then this is the number of pages used for the table, otherwise it is the number of pages used for the current index.
     */
    @JsonAlias({"PAGES"})
    private Long pages;

    /**
     * Filter condition, if any. (may be null)
     */
    @JsonAlias({"FILTER_CONDITION"})
    private String filterCondition;


    private Long subPart;


    private String editStatus;
}

