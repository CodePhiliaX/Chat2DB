
package ai.chat2db.server.admin.api.controller.datasource;

import ai.chat2db.server.admin.api.controller.datasource.converter.DataSourceAdminConverter;
import ai.chat2db.server.admin.api.controller.datasource.request.DataSourceCloneRequest;
import ai.chat2db.server.admin.api.controller.datasource.request.DataSourceCreateRequest;
import ai.chat2db.server.admin.api.controller.datasource.request.DataSourcePageQueryRequest;
import ai.chat2db.server.admin.api.controller.datasource.request.DataSourceUpdateRequest;
import ai.chat2db.server.admin.api.controller.datasource.vo.DataSourcePageQueryVO;
import ai.chat2db.server.domain.api.param.DataSourceCreateParam;
import ai.chat2db.server.domain.api.param.DataSourceUpdateParam;
import ai.chat2db.server.domain.api.service.DataSourceService;
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
 * Data Source Management
 *
 * @author Jiaju Zhuang
 */
@RequestMapping("/api/admin/data/source")
@RestController
public class DataSourceController {

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
    public WebPageResult<DataSourcePageQueryVO> page(@Valid DataSourcePageQueryRequest request) {
        return dataSourceService.queryPage(dataSourceAdminConverter.request2param(request), null)
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
    public DataResult<Long> create(@RequestBody DataSourceCreateRequest request) {
        DataSourceCreateParam param = dataSourceAdminConverter.createReq2param(request);
        return dataSourceService.create(param);
    }

    /**
     * update
     *
     * @param request
     * @return
     * @version 2.1.0
     */
    @PostMapping("/update")
    public ActionResult update(@RequestBody DataSourceUpdateRequest request) {
        DataSourceUpdateParam param = dataSourceAdminConverter.updateReq2param(request);
        return dataSourceService.update(param);
    }

    /**
     * clone
     *
     * @param request
     * @return
     */
    @PostMapping("/clone")
    public DataResult<Long> clone(@RequestBody DataSourceCloneRequest request) {
        return dataSourceService.copyById(request.getId());
    }

    /**
     * delete
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable Long id) {
        return dataSourceService.delete(id);
    }
}
