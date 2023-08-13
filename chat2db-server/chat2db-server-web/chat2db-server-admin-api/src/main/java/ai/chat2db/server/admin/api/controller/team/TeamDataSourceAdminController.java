
package ai.chat2db.server.admin.api.controller.team;

import ai.chat2db.server.admin.api.controller.team.converter.TeamDataSourcesAdminConverter;
import ai.chat2db.server.admin.api.controller.team.request.TeamDataSourceBatchCreateRequest;
import ai.chat2db.server.admin.api.controller.team.vo.TeamDataSourcePageQueryVO;
import ai.chat2db.server.common.api.controller.request.CommonPageQueryRequest;
import ai.chat2db.server.domain.api.enums.AccessObjectTypeEnum;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessCreatParam;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessSelector;
import ai.chat2db.server.domain.api.service.DataSourceAccessService;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
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
 * Team Data Source Management
 *
 * @author Jiaju Zhuang
 */
@RequestMapping("/api/admin/team/data_source")
@RestController
public class TeamDataSourceAdminController {
    private static final DataSourceAccessSelector DATA_SOURCE_ACCESS_SELECTOR = DataSourceAccessSelector.builder()
        .accessObject(Boolean.TRUE)
        .build();

    @Resource
    private DataSourceAccessService dataSourceAccessService;
    @Resource
    private TeamDataSourcesAdminConverter teamDataSourcesAdminConverter;

    /**
     * Pagination query
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @GetMapping("/page")
    public WebPageResult<TeamDataSourcePageQueryVO> page(@Valid CommonPageQueryRequest request) {
        return dataSourceAccessService.comprehensivePageQuery(teamDataSourcesAdminConverter.request2param(request),
                DATA_SOURCE_ACCESS_SELECTOR)
            .mapToWeb(teamDataSourcesAdminConverter::dto2vo);
    }

    /**
     * create
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @PostMapping("/batch_create")
    public ActionResult create(@Valid @RequestBody TeamDataSourceBatchCreateRequest request) {
        request.getDataSourceIdList()
            .forEach(dataSourceId -> dataSourceAccessService.create(DataSourceAccessCreatParam.builder()
                .dataSourceId(dataSourceId)
                .accessObjectId(request.getTeamId())
                .accessObjectType(AccessObjectTypeEnum.TEAM.getCode())
                .build()));
        return ActionResult.isSuccess();
    }

    /**
     * delete
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable Long id) {
        return dataSourceAccessService.delete(id);
    }
}
