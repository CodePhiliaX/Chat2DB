package ai.chat2db.server.domain.core.impl;

import ai.chat2db.server.domain.api.enums.AccessObjectTypeEnum;
import ai.chat2db.server.domain.api.enums.DataSourceKindEnum;
import ai.chat2db.server.domain.api.model.DataSource;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessPageQueryParam;
import ai.chat2db.server.domain.api.service.DataSourceAccessBusinessService;
import ai.chat2db.server.domain.api.service.DataSourceAccessService;
import ai.chat2db.server.domain.repository.mapper.DataSourceAccessCustomMapper;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.common.exception.PermissionDeniedBusinessException;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
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
    public ActionResult checkPermission(@NotNull DataSource dataSource) {
        LoginUser loginUser = ContextUtils.getLoginUser();
        // private
        if (DataSourceKindEnum.PRIVATE.getCode().equals(dataSource.getKind())) {
            if (loginUser.getId().equals(dataSource.getUserId())) {
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
        dataSourceAccessPageQueryParam.setDataSourceId(dataSource.getId());
        dataSourceAccessPageQueryParam.setAccessObjectType(AccessObjectTypeEnum.USER.getCode());
        dataSourceAccessPageQueryParam.setAccessObjectId(loginUser.getId());
        dataSourceAccessPageQueryParam.queryOne();
        if (dataSourceAccessService.pageQuery(dataSourceAccessPageQueryParam, null).hasData()) {
            return ActionResult.isSuccess();
        }

        // Verify if the team has permission
        if (dataSourceAccessCustomMapper.checkTeamPermission(dataSource.getId(), loginUser.getId()) != null) {
            return ActionResult.isSuccess();

        }
        throw new PermissionDeniedBusinessException();
    }
}
