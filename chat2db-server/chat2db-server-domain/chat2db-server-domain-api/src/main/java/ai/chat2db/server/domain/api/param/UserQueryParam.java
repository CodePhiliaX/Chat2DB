package ai.chat2db.server.domain.api.param;

import java.io.Serial;

import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 用户查询参数
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryParam extends PageQueryParam {
    @Serial
    private static final long serialVersionUID = 7341467383637825621L;
    /**
     * 用户名
     */
    private String keyWord;
}
