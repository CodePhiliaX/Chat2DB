
package ai.chat2db.server.common.api.controller;

import ai.chat2db.server.common.api.controller.converter.EnvironmentCommonConverter;
import ai.chat2db.server.common.api.controller.vo.SimpleEnvironmentVO;
import ai.chat2db.server.domain.api.param.EnvironmentPageQueryParam;
import ai.chat2db.server.domain.api.service.EnvironmentService;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Basic interface
 *
 * @author Jiaju Zhuang
 */
@RequestMapping("/api/common")
@RestController
public class CommonCommonController {

    @Resource
    private EnvironmentService environmentService;
    @Resource
    private EnvironmentCommonConverter environmentCommonConverter;

    /**
     * Query all environments
     *
     * @return
     * @version 2.1.0
     */
    @GetMapping("/environment/list_all")
    public ListResult<SimpleEnvironmentVO> environmentList() {
        EnvironmentPageQueryParam environmentPageQueryParam = new EnvironmentPageQueryParam();
        environmentPageQueryParam.setPageSize(Integer.MIN_VALUE);
        return ListResult.of(
            environmentCommonConverter.dto2vo(environmentService.pageQuery(environmentPageQueryParam).getData()));
    }

}
