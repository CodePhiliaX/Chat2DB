
package ai.chat2db.server.admin.api.controller.datasource;

import ai.chat2db.server.admin.api.controller.datasource.converter.DataSourceAdminConverter;
import ai.chat2db.server.admin.api.controller.datasource.request.DataSourceCloneRequest;
import ai.chat2db.server.admin.api.controller.datasource.request.DataSourceCreateRequest;
import ai.chat2db.server.admin.api.controller.datasource.request.DataSourceUpdateRequest;
import ai.chat2db.server.admin.api.controller.datasource.vo.DataSourcePageQueryVO;
import ai.chat2db.server.common.api.controller.request.CommonPageQueryRequest;
import ai.chat2db.server.domain.api.param.datasource.DataSourceCreateParam;
import ai.chat2db.server.domain.api.param.datasource.DataSourcePageQueryParam;
import ai.chat2db.server.domain.api.param.datasource.DataSourcePageQueryParam.OrderCondition;
import ai.chat2db.server.domain.api.param.datasource.DataSourceSelector;
import ai.chat2db.server.domain.api.param.datasource.DataSourceUpdateParam;
import ai.chat2db.server.domain.api.service.DataSourceService;
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
 * Data Source Management
 *
 * @author Jiaju Zhuang
 */
@RequestMapping("/api/admin/data_source")
@RestController
public class DataSourceAdminController {

    private static final DataSourceSelector DATA_SOURCE_SELECTOR = DataSourceSelector.builder()
        .environment(Boolean.TRUE)
        .build();
    @Resource
    private DataSourceService dataSourceService;
    @Resource
    private DataSourceAdminConverter dataSourceAdminConverter;

    /**
     * Pagination query
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @GetMapping("/page")
    public WebPageResult<DataSourcePageQueryVO> page(@Valid CommonPageQueryRequest request) {
        DataSourcePageQueryParam param = dataSourceAdminConverter.request2param(request);
        param.orderBy(OrderCondition.ID_DESC);
        return dataSourceService.queryPageWithPermission(param, DATA_SOURCE_SELECTOR)
            .mapToWeb(dataSourceAdminConverter::dto2vo);
    }

    /**
     * create
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @PostMapping("/create")
    public DataResult<Long> create(@Valid @RequestBody DataSourceCreateRequest request) {
        DataSourceCreateParam param = dataSourceAdminConverter.createReq2param(request);
        return dataSourceService.createWithPermission(param);
    }

    /**
     * update
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @PostMapping("/update")
    public DataResult<Long> update(@Valid @RequestBody DataSourceUpdateRequest request) {
        DataSourceUpdateParam param = dataSourceAdminConverter.updateReq2param(request);
        return dataSourceService.updateWithPermission(param);
    }

    /**
     * clone
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @PostMapping("/clone")
    public DataResult<Long> clone(@RequestBody DataSourceCloneRequest request) {
        return dataSourceService.copyByIdWithPermission(request.getId());
    }

    /**
     * delete
     *
     * @param id
     * @return
     * @version 2.1.0
     */
    @DeleteMapping("/{id}")
    public DataResult<Boolean> delete(@PathVariable Long id) {
        return dataSourceService.deleteWithPermission(id).toBooleaSuccessnDataResult();
    }
}
