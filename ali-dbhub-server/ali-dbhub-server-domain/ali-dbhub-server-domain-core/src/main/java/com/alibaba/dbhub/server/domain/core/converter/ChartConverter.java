package com.alibaba.dbhub.server.domain.core.converter;

import java.util.List;

import com.alibaba.dbhub.server.domain.api.model.Chart;
import com.alibaba.dbhub.server.domain.api.param.ChartCreateParam;
import com.alibaba.dbhub.server.domain.api.param.ChartUpdateParam;
import com.alibaba.dbhub.server.domain.repository.entity.ChartDO;

import org.mapstruct.Mapper;

/**
 * @author moji
 * @version ChartConverter.java, v 0.1 2023年06月09日 17:13 moji Exp $
 * @date 2023/06/09
 */
@Mapper(componentModel = "spring")
public abstract class ChartConverter {

    /**
     * 参数转换
     *
     * @param param
     * @return
     */
    public abstract ChartDO param2do(ChartCreateParam param);

    /**
     * 参数转换
     *
     * @param param
     * @return
     */
    public abstract ChartDO updateParam2do(ChartUpdateParam param);

    /**
     * 模型转换
     *
     * @param chartDO
     * @return
     */
    public abstract Chart do2model(ChartDO chartDO);

    /**
     * 模型转换
     *
     * @param chartDOS
     * @return
     */
    public abstract List<Chart> do2model(List<ChartDO> chartDOS);
}
