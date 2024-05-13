package ai.chat2db.spi.model;


import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * Column information
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TableIndexColumn implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Index name
     */
    @JsonAlias({"INDEX_NAME"})
    private String indexName;

    /**
     * Table Name
     */
    @JsonAlias ({"TABLE_NAME"})
    private String tableName;

    /**
     * Index type
     *
     * @see
     */
    private String type;

    /**
     * Comment
     */
    private String comment;

    /**
     * columnName
     */
    @JsonAlias({"COLUMN_NAME"})
    private String columnName;

    /**
     * ordinalPosition
     */
    @JsonAlias({"ORDINAL_POSITION"})
    private Short ordinalPosition;

    /**
     * collation
     *
     */
    private String collation;


    /**
     * The schema to which the index belongs
     */
    @JsonAlias({"TABLE_SCHEM"})
    private String schemaName;

    /**
     * Database name
     */
    @JsonAlias({"TABLE_CAT"})
    private String databaseName;

    /**
     * Is it unique?
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

