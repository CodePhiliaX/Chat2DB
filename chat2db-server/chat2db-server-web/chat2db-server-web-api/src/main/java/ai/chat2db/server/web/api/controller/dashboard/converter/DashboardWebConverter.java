package ai.chat2db.server.web.api.controller.dashboard.converter;

import java.util.List;

import ai.chat2db.server.domain.api.model.Dashboard;
import ai.chat2db.server.domain.api.param.dashboard.DashboardCreateParam;
import ai.chat2db.server.domain.api.param.dashboard.DashboardUpdateParam;
import ai.chat2db.server.web.api.controller.dashboard.request.DashboardCreateRequest;
import ai.chat2db.server.web.api.controller.dashboard.request.DashboardUpdateRequest;
import ai.chat2db.server.web.api.controller.dashboard.vo.DashboardVO;

import org.mapstruct.Mapper;

/**
 * @author moji
 * @version DashboardWebConverter.java, v 0.1 2023年06月09日 15:45 moji Exp $
 * @date 2023/06/09
 */
@Mapper(componentModel = "spring")
public abstract class DashboardWebConverter {

    /**
     * 模型转换
     *
     * @param dashboard
     * @return
     */
    public abstract DashboardVO model2vo(Dashboard dashboard);

    /**
     * 模型转换
     *
     * @param dashboards
     * @return
     */
    public abstract List<DashboardVO> model2vo(List<Dashboard> dashboards);

    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract DashboardCreateParam req2param(DashboardCreateRequest request);

    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract DashboardUpdateParam req2updateParam(DashboardUpdateRequest request);
}
