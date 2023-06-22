package ai.chat2db.server.web.api.controller.data.source.vo;

import lombok.Data;

/**
 * @author moji
 * @version DatabaseVO.java, v 0.1 2022年09月16日 17:24 moji Exp $
 * @date 2022/09/16
 */
@Data
public class DatabaseVO {

    /**
     * DB名称
     */
    private String name;

    /**
     * DB描述
     */
    private String description;

    /**
     * DB下表数量或key数量
     */
    private Integer count;
}
