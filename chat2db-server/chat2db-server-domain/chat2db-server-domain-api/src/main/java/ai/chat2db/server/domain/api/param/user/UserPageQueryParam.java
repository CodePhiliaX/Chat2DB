package ai.chat2db.server.domain.api.param.user;

import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * * page query
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserPageQueryParam extends PageQueryParam {

    /**
     * searchKey
     */
    private String searchKey;
}
