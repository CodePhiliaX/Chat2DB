package ai.chat2db.server.web.api.controller.data.source.vo;

import lombok.Data;

/**
 * @author moji
 * @version EnvVO.java, v 0.1 2022年09月18日 14:06 moji Exp $
 * @date 2022/09/18
 */
@Data
public class EnvVO {

    /**
     * 环境code
     */
    private String code;

    /**
     * 环境名称
     */
    private String name;
}
