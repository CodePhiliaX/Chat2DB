package ai.chat2db.server.web.api.controller.rdb.request;

import java.util.List;

import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.model.TableIndex;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 修改表sql请求
 *
 * @author Shi Yi
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TableRequest {
    /**
     * Table Name
     */
    private String name;

    /**
     * Table description
     */
    private String comment;

    /**
     * Column
     */
    private List<TableColumn> columnList;

    /**
     * index
     */
    private List<TableIndex> indexList;


    /**
     * Space name
     */
    private String schemaName;

    /**
     * Database name
     */
    private String databaseName;


    private String engine;


    private String charset;


    private String collate;

    private Long incrementValue;

    private String partition;

}
