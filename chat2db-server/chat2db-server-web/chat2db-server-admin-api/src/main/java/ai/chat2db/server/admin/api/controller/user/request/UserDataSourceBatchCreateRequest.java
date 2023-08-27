package ai.chat2db.server.admin.api.controller.user.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * create
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserDataSourceBatchCreateRequest {

    /**
     * user id
     */
    private Long userId;

    /**
     * Data Source id list
     */
    @NotNull
    private List<Long> dataSourceIdList;
}
