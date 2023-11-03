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
 * @author 是仪
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TableRequest {
    /**
     * 表名称
     */
    private String name;

    /**
     * 表描述
     */
    private String comment;

    /**
     * 列
     */
    private List<TableColumn> columnList;

    /**
     * 索引
     */
    private List<TableIndex> indexList;


    /**
     * 空间名
     */
    private String schemaName;

    /**
     * 数据库名
     */
    private String databaseName;


    private String engine;


    private String charset;


    private String collate;

    private Long incrementValue;

    private String partition;

}
