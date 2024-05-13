package ai.chat2db.server.web.api.controller.rdb.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Column information
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IndexColumnVO {

    /**
     * Index name
     */
    private String indexName;

    /**
     * Table Name
     */
    private String tableName;

    /**
     * index type
     *
     * @see
     */
    private String type;

    /**
     * Comment
     */
    private String comment;

    /**
     * Column name
     */
    private String columnName;

    /**
     * order
     */
    private Short ordinalPosition;

    /**
     * sort
     *
     */
    private String collation;


    /**
     * The schema to which the index belongs
     */
    private String schemaName;

    /**
     * Database name
     */
    private String databaseName;

    /**
     * Is it unique?
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

