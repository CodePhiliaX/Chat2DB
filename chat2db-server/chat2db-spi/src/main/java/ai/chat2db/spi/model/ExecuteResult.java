package ai.chat2db.spi.model;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Results of the
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteResult  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * success flag
     */
    private Boolean success;

    /**
     * Failure message prompt
     * Only in case of failure
     */
    private String message;

    /**
     * executed sql
     */
    private String sql;

    /**
     * Original SQL without pagination
     */
    private String originalSql;

    /**
     * description
     */
    private String description;

    /**
     * Modify the number of rows and query sql will not return
     */
    private Integer updateCount;

    /**
     * List of display headers
     */
    private List<Header> headerList;

    /**
     * list of data
     */
    private List<List<String>> dataList;

    /**
     * sql type
     *
     * @see ai.chat2db.spi.enums.SqlTypeEnum
     */
    private String sqlType;

    /**
     * Whether there is a next page
     * Only available for select statements
     */
    private Boolean hasNextPage;

    /**
     * Page coding
     * Only available for select statements
     */
    private Integer pageNo;

    /**
     * Paging Size
     * Only available for select statements
     */
    private Integer pageSize;

    /**
     * Total number of fuzzy rows
     * Only select statements have
     */
    private String fuzzyTotal;

    /**
     * execution duration
     */
    private Long duration;


    /**
     * Whether the returned result can be edited
     */
    private boolean canEdit;

    /**
     * Table Name
     */
    private String tableName;
}
