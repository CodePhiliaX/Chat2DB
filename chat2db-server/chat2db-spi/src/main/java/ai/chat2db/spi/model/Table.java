package ai.chat2db.spi.model;

import java.util.List;


import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 表信息
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Table {

    /**
     * 表名
     */
    @JsonAlias({"TABLE_NAME"})
    private String name;

    /**
     * 描述
     */
    @JsonAlias({"REMARKS"})

    private String comment;

    /**
     * DB 名
     */
    @JsonAlias({"TABLE_SCHEM"})

    private String schemaName;

    /**
     * 列列表
     */
    private List<TableColumn> columnList;

    /**
     * 索引列表
     */
    private List<TableIndex> indexList;

    /**
     * DB类型
     */
    private String dbType;

    /**
     * 数据库名
     */
    @JsonAlias("TABLE_CAT")
    private String databaseName;

    /**
     * 表类型
     */
    @JsonAlias("TABLE_TYPE")
    private String type;

    /**
     * 是否置顶
     */
    private boolean pinned;

    /**
     * ddl
     */
    private String ddl;


    private String engine;


    private String charset;


    private String collate;


    private Long incrementValue;


    private String partition;


    private String tablespace;
}

