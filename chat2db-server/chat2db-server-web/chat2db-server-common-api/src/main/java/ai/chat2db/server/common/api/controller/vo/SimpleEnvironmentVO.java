package ai.chat2db.server.common.api.controller.vo;

import java.io.Serial;
import java.io.Serializable;

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
public class SimpleEnvironmentVO implements Serializable {

    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;

    /**
     * primary key
     */
    private Long id;

    /**
     * environment name
     */
    private String name;

    /**
     * environment abbreviation
     */
    private String shortName;

    /**
     * color
     */
    private String color;
}
