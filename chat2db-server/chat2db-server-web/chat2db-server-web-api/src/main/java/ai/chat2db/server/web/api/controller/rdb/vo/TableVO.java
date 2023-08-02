package ai.chat2db.server.web.api.controller.rdb.vo;

import java.util.List;

import lombok.Data;

/**
 * @author moji
 * @version TableVO.java, v 0.1 2022年09月16日 17:16 moji Exp $
 * @date 2022/09/16
 */
@Data
public class TableVO {

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
    private List<ColumnVO> columnList;

    /**
     * 索引
     */
    private List<IndexVO> indexList;

    /**
     * 是否已经被固定
     */
    private boolean pinned;
}
