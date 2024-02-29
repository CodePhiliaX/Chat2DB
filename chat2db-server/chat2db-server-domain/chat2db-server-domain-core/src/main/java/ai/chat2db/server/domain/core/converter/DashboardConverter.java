package ai.chat2db.server.domain.core.converter;

import java.util.List;

import ai.chat2db.server.domain.api.model.Dashboard;
import ai.chat2db.server.domain.api.param.dashboard.DashboardCreateParam;
import ai.chat2db.server.domain.api.param.dashboard.DashboardUpdateParam;
import ai.chat2db.server.domain.repository.entity.DashboardDO;

import org.mapstruct.Mapper;

/**
 * @author moji
 * @version ChartConverter.java, v 0.1 June 9, 2023 17:13 moji Exp $
 * @date 2023/06/09
 */
@Mapper(componentModel = "spring")
public abstract class DashboardConverter {

    /**
     * Parameter conversion
     *
     * @param param
     * @return
     */
    public abstract DashboardDO param2do(DashboardCreateParam param);

    /**
     * Parameter conversion
     *
     * @param param
     * @return
     */
    public abstract DashboardDO updateParam2do(DashboardUpdateParam param);

    /**
     * Model conversion
     *
     * @param chartDO
     * @return
     */
    public abstract Dashboard do2model(DashboardDO chartDO);

    /**
     * Model conversion
     *
     * @param chartDOS
     * @return
     */
    public abstract List<Dashboard> do2model(List<DashboardDO> chartDOS);
}
