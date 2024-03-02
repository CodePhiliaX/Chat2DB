package ai.chat2db.server.web.api.controller.data.source.vo;

import lombok.Data;

/**
 * @author moji
 * @version DatabaseVO.java, v 0.1 September 16, 2022 17:24 moji Exp $
 * @date 2022/09/16
 */
@Data
public class DatabaseVO {

    /**
     * DB name
     */
    private String name;

    /**
     * DB description
     */
    private String description;

    /**
     * The number of tables or keys under DB
     */
    private Integer count;
}
