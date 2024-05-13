package ai.chat2db.server.web.api.controller.data.source.vo;

import lombok.Data;

/**
 * @author moji
 * @version EnvVO.java, v 0.1 September 18, 2022 14:06 moji Exp $
 * @date 2022/09/18
 */
@Data
public class EnvVO {

    /**
     * environment code
     */
    private String code;

    /**
     * environment name
     */
    private String name;
}
