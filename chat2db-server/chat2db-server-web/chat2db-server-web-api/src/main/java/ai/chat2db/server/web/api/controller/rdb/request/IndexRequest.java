package ai.chat2db.server.web.api.controller.rdb.request;

import java.util.List;

import ai.chat2db.spi.enums.IndexTypeEnum;

import ai.chat2db.spi.model.TableIndexColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * index
 *
 * @author Shi Yi
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IndexRequest {

    /**
     * Index name
     */
    private String name;

    /**
     * all types
     *
     * @see IndexTypeEnum
     */
    private String type;

    /**
     * Comment
     */
    private String comment;

    /**
     * Columns included in the index
     */
    private List<TableIndexColumn> columnList;


    private String editStatus;

}
