package ai.chat2db.server.common.api.controller.vo;

import java.io.Serial;
import java.io.Serializable;

import ai.chat2db.server.domain.api.enums.EnvironmentStyleEnum;
import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Environment
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentVO implements Serializable {

    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;

    /**
     * 主键
     */
    private Long id;

    /**
     * 环境名称
     */
    private String name;

    /**
     * 环境缩写
     */
    private String shortName;

    /**
     * 样式类型
     *
     * @see EnvironmentStyleEnum
     */
    private String style;
}
