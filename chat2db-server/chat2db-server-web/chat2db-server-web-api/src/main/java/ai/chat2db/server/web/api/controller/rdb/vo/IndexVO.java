package ai.chat2db.server.web.api.controller.rdb.vo;

import java.util.List;

import ai.chat2db.spi.enums.IndexTypeEnum;

import lombok.Data;

/**
 * @author moji
 * @version IndexVO.java, v 0.1 September 16, 2022 17:47 moji Exp $
 * @date 2022/09/16
 */
@Data
public class IndexVO {

    /**
     * Contains columns
     */
    private String columns;

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
    private List<IndexColumnVO> columnList;
}
