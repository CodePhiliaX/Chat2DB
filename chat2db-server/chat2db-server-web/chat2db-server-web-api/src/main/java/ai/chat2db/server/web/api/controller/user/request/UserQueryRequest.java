
package ai.chat2db.server.web.api.controller.user.request;

import java.io.Serial;

import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : UserQueyRequest.java
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryRequest extends PageQueryParam {

    @Serial
    private static final long serialVersionUID = 5663790872812326134L;
    /**
     * 用户名魔化搜索
     */
    private String keyWord;
}