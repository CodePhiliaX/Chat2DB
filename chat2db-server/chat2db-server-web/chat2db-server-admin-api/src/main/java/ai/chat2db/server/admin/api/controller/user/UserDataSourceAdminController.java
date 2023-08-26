
package ai.chat2db.server.admin.api.controller.user;

import ai.chat2db.server.admin.api.controller.user.converter.UserDataSourcesAdminConverter;
import ai.chat2db.server.admin.api.controller.user.request.UserDataSourceBatchCreateRequest;
import ai.chat2db.server.admin.api.controller.user.request.UserPageCommonQueryRequest;
import ai.chat2db.server.admin.api.controller.user.vo.UserDataSourcePageQueryVO;
import ai.chat2db.server.domain.api.enums.AccessObjectTypeEnum;
import ai.chat2db.server.domain.api.param.datasource.DataSourceSelector;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessCreatParam;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessPageQueryParam;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessSelector;
import ai.chat2db.server.domain.api.service.DataSourceAccessService;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.web.WebPageResult;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User Data Source Management
 *
 * @author Jiaju Zhuang
 */
@RequestMapping("/api/admin/user/data_source")
@RestController
public class UserDataSourceAdminController {
    private static final DataSourceAccessSelector DATA_SOURCE_ACCESS_SELECTOR = DataSourceAccessSelector.builder()
        .dataSource(Boolean.TRUE)
        .dataSourceSelector(DataSourceSelector.builder()
            .environment(Boolean.TRUE)
            .build())
        .build();

    @Resource
    private DataSourceAccessService dataSourceAccessService;
    @Resource
    private UserDataSourcesAdminConverter userDataSourcesAdminConverter;

    /**
     * Pagination query
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @GetMapping("/page")
    public WebPageResult<UserDataSourcePageQueryVO> page(@Valid UserPageCommonQueryRequest request) {
        return dataSourceAccessService.comprehensivePageQuery(userDataSourcesAdminConverter.request2param(request),
                DATA_SOURCE_ACCESS_SELECTOR)
            .mapToWeb(userDataSourcesAdminConverter::dto2vo);
    }

    /**
     * create
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @PostMapping("/batch_create")
    public ActionResult create(@RequestBody UserDataSourceBatchCreateRequest request) {
        request.getDataSourceIdList()
            .forEach(dataSourceId -> {
                DataSourceAccessPageQueryParam dataSourceAccessPageQueryParam = new DataSourceAccessPageQueryParam();
                dataSourceAccessPageQueryParam.setDataSourceId(dataSourceId);
                dataSourceAccessPageQueryParam.setAccessObjectType(AccessObjectTypeEnum.USER.getCode());
                dataSourceAccessPageQueryParam.setAccessObjectId(request.getUserId());
                dataSourceAccessPageQueryParam.queryOne();
                if (dataSourceAccessService.pageQuery(dataSourceAccessPageQueryParam, null).hasData()) {
                    return;
                }
                dataSourceAccessService.create(DataSourceAccessCreatParam.builder()
                    .dataSourceId(dataSourceId)
                    .accessObjectId(request.getUserId())
                    .accessObjectType(AccessObjectTypeEnum.USER.getCode())
                    .build());
            });
        return ActionResult.isSuccess();
    }

    /**
     * delete
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public DataResult<Boolean> delete(@PathVariable Long id) {
        return dataSourceAccessService.delete(id).toBooleaSuccessnDataResult();
    }
}
