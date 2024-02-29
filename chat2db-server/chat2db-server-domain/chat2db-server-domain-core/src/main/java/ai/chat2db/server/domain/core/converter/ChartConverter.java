package ai.chat2db.server.domain.core.converter;

import java.util.List;

import ai.chat2db.server.domain.api.model.Chart;
import ai.chat2db.server.domain.api.chart.ChartCreateParam;
import ai.chat2db.server.domain.api.chart.ChartUpdateParam;
import ai.chat2db.server.domain.repository.entity.ChartDO;

import org.mapstruct.Mapper;

/**
 * @author moji
 * @version ChartConverter.java, v 0.1 June 9, 2023 17:13 moji Exp $
 * @date 2023/06/09
 */
@Mapper(componentModel = "spring")
public abstract class ChartConverter {

    /**
     * Parameter conversion
     *
     * @param param
     * @return
     */
    public abstract ChartDO param2do(ChartCreateParam param);

    /**
     * Parameter conversion
     *
     * @param param
     * @return
     */
    public abstract ChartDO updateParam2do(ChartUpdateParam param);

    /**
     * Model conversion
     *
     * @param chartDO
     * @return
     */
    public abstract Chart do2model(ChartDO chartDO);

    /**
     * Model conversion
     *
     * @param chartDOS
     * @return
     */
    public abstract List<Chart> do2model(List<ChartDO> chartDOS);
}
