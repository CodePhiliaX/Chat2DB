
package ai.chat2db.server.admin.api.controller.datasource;

import ai.chat2db.server.admin.api.controller.datasource.converter.DataSourceAccessAdminConverter;
import ai.chat2db.server.admin.api.controller.datasource.request.DataSourceAccessBatchCreateRequest;
import ai.chat2db.server.admin.api.controller.datasource.request.DataSourceAccessPageQueryRequest;
import ai.chat2db.server.admin.api.controller.datasource.vo.DataSourceAccessPageQueryVO;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessCreatParam;
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
 * Data Source Access Management
 *
 * @author Jiaju Zhuang
 */
@RequestMapping("/api/admin/data_source/access")
@RestController
public class DataSourceAccessAdminController {

    private static final DataSourceAccessSelector DATA_SOURCE_ACCESS_SELECTOR = DataSourceAccessSelector.builder()
        .accessObject(Boolean.TRUE)
        .build();

    @Resource
    private DataSourceAccessService dataSourceAccessService;
    @Resource
    private DataSourceAccessAdminConverter dataSourceAccessAdminConverter;

    /**
     * Pagination query
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @GetMapping("/page")
    public WebPageResult<DataSourceAccessPageQueryVO> page(@Valid DataSourceAccessPageQueryRequest request) {
        return dataSourceAccessService.comprehensivePageQuery(dataSourceAccessAdminConverter.request2param(request),
                DATA_SOURCE_ACCESS_SELECTOR)
            .mapToWeb(dataSourceAccessAdminConverter::dto2vo);
    }

    /**
     * batch
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @PostMapping("/batch_create")
    public ActionResult batchCreate(@Valid @RequestBody DataSourceAccessBatchCreateRequest request) {
        request.getAccessObjectList()
            .forEach(accessObject -> dataSourceAccessService.create(DataSourceAccessCreatParam.builder()
                .dataSourceId(request.getDataSourceId())
                .accessObjectId(accessObject.getId())
                .accessObjectType(accessObject.getType())
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
    public DataResult<Boolean> delete(@PathVariable Long id) {
        return dataSourceAccessService.delete(id).toBooleaSuccessnDataResult();
    }

}
