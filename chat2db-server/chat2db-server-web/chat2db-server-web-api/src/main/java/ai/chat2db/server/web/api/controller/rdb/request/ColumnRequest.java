package ai.chat2db.server.web.api.controller.rdb.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Column
 *
 * @author Shi Yi
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnRequest {

    /**
     * The old column name, this parameter is needed when modifying the column
     * You can send it without modification
     */
    private String oldName;
    /**
     * name
     */
    private String name;

    /**
     * Column type
     * For example, varchar(100), double(10,6)
     */
    private String columnType;

    /**
     * Is it empty
     */
    private Integer nullable;

    /**
     * Is it a primary key?
     */
    private Boolean primaryKey;

    /**
     * default value
     */
    private String defaultValue;

    /**
     * Whether to increment automatically
     */
    private Boolean autoIncrement;

    /**
     * comment
     */
    private String comment;
}
