package ai.chat2db.server.web.api.controller.rdb.vo;

import java.util.List;

import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.model.TableIndex;
import lombok.Data;

/**
 * @author moji
 * @version TableVO.java, v 0.1 September 16, 2022 17:16 moji Exp $
 * @date 2022/09/16
 */
@Data
public class TableVO {

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
     * Has it been fixed?
     */
    private boolean pinned;

    /**
     * ddl
     */
    private String ddl;
}
