package ai.chat2db.spi.model;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Index information
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
     * Index name
     */
    private String name;

    /**
     * Table Name
     */
    private String tableName;

    /**
     * Index type
     *
     * @see IndexTypeEnum
     */
    private String type;

    /**
     * Is it unique?
     */
    private Boolean unique;

    /**
     * Comment
     */
    private String comment;

    /**
     * The schema to which the index belongs
     */
    private String schemaName;

    /**
     * Database name
     */
    private String databaseName;

    /**
     * Columns included in the index
     */
    private List<TableIndexColumn> columnList;


    private String editStatus;

    /**
     * Is it concurrent?
     */
    private Boolean concurrently;

    /**
     * Index method
     */
    private String method;


    /**
     * Foreign key points to schema
     */
    private String foreignSchemaName;

    /**
     * Foreign key points to table name
     */
    private String foreignTableName;

    /**
     * The column name pointed to by the foreign key
     */
    private List<String> foreignColumnNamelist;

}
