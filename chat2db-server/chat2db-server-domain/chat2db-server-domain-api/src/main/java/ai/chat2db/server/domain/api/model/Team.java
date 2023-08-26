package ai.chat2db.server.domain.api.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Team
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Team implements Serializable {

    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;

    /**
     * 主键
     */
    @NotNull
    private Long id;

    /**
     * 团队编码
     */
    @NotNull
    private String code;

    /**
     * 团队名称
     */
    @NotNull
    private String name;

    /**
     * 团队状态
     *
     * @see ai.chat2db.server.domain.api.enums.ValidStatusEnum
     */
    @NotNull
    private String status;

    /**
     * 团队描述
     */
    private String description;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 修改人用户id
     */
    private Long modifiedUserId;

    /**
     * 修改人用户
     */
    private User modifiedUser;

}
