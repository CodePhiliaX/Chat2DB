package ai.chat2db.server.domain.core.util;

import ai.chat2db.server.domain.api.enums.RoleCodeEnum;
import ai.chat2db.server.tools.common.exception.PermissionDeniedBusinessException;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;

/**
 * Permission Utils
 *
 * @author Jiaju Zhuang
 */
public class PermissionUtils {

    /**
     * Verify whether the currently logged in user has permission to operate on the current content
     *
     * @param createUserId The creator of the current content
     */
    public static void checkPermission(Long createUserId) {
        LoginUser loginUser = ContextUtils.getLoginUser();
        // Representative is desktop mode
        if (RoleCodeEnum.DESKTOP.getDefaultUserId().equals(loginUser.getId())) {
            if (RoleCodeEnum.DESKTOP.getDefaultUserId().equals(createUserId)) {
                return;
            } else {
                throw new PermissionDeniedBusinessException();
            }
        }
        // Administrators can edit anything
        if (loginUser.getAdmin()) {
            return;
        }
        // Not that administrators can only edit their own things
        if (!loginUser.getId().equals(createUserId)) {
            throw new PermissionDeniedBusinessException();
        }
    }
}
