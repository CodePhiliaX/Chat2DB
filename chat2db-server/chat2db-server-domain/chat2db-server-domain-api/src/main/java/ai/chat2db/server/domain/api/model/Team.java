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
     * primary key
     */
    @NotNull
    private Long id;

    /**
     * team coding
     */
    @NotNull
    private String code;

    /**
     * Team Name
     */
    @NotNull
    private String name;

    /**
     * Team status
     *
     * @see ai.chat2db.server.domain.api.enums.ValidStatusEnum
     */
    @NotNull
    private String status;

    /**
     * Team description
     */
    private String description;

    /**
     * modified time
     */
    private Date gmtModified;

    /**
     * Modifier user id
     */
    private Long modifiedUserId;

    /**
     * Modifier user
     */
    private User modifiedUser;

}
