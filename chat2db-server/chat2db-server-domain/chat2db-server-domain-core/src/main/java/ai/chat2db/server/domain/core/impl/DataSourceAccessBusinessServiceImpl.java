package ai.chat2db.server.domain.core.impl;

import ai.chat2db.server.domain.api.enums.AccessObjectTypeEnum;
import ai.chat2db.server.domain.api.enums.RoleCodeEnum;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessPageQueryParam;
import ai.chat2db.server.domain.api.service.DataSourceAccessBusinessService;
import ai.chat2db.server.domain.api.service.DataSourceAccessService;
import ai.chat2db.server.domain.repository.mapper.DataSourceAccessCustomMapper;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.common.exception.PermissionDeniedBusinessException;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Data Source Access
 *
 * @author Jiaju Zhuang
 */
@Slf4j
@Service
public class DataSourceAccessBusinessServiceImpl implements DataSourceAccessBusinessService {

    @Resource
    private DataSourceAccessService dataSourceAccessService;
    @Resource
    private DataSourceAccessCustomMapper dataSourceAccessCustomMapper;

    @Override
    public ActionResult checkPermission(Long dataSourceId) {
        LoginUser loginUser = ContextUtils.getLoginUser();
        // Representative is desktop mode
        if (RoleCodeEnum.DESKTOP.getDefaultUserId().equals(loginUser.getId())) {
            if (RoleCodeEnum.DESKTOP.getDefaultUserId().equals(dataSourceId)) {
                return ActionResult.isSuccess();
            } else {
                throw new PermissionDeniedBusinessException();
            }
        }

        // Administrators can edit anything
        if (loginUser.getAdmin()) {
            return ActionResult.isSuccess();
        }

        // Verify if user have permission
        DataSourceAccessPageQueryParam dataSourceAccessPageQueryParam = new DataSourceAccessPageQueryParam();
        dataSourceAccessPageQueryParam.setDataSourceId(dataSourceId);
        dataSourceAccessPageQueryParam.setAccessObjectType(AccessObjectTypeEnum.USER.getCode());
        dataSourceAccessPageQueryParam.setAccessObjectId(loginUser.getId());
        dataSourceAccessPageQueryParam.queryOne();
        if (dataSourceAccessService.pageQuery(dataSourceAccessPageQueryParam, null).hasData()) {
            return ActionResult.isSuccess();
        }

        // Verify if the team has permission
        if (dataSourceAccessCustomMapper.checkTeamPermission(dataSourceId, loginUser.getId()) != null) {
            return ActionResult.isSuccess();

        }
        throw new PermissionDeniedBusinessException();
    }
}
