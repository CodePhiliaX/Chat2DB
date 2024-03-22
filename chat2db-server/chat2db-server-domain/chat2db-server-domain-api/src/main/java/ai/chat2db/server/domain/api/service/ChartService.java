package ai.chat2db.server.domain.api.service;

import java.util.List;

import ai.chat2db.server.domain.api.chart.ChartCreateParam;
import ai.chat2db.server.domain.api.chart.ChartListQueryParam;
import ai.chat2db.server.domain.api.chart.ChartQueryParam;
import ai.chat2db.server.domain.api.chart.ChartUpdateParam;
import ai.chat2db.server.domain.api.model.Chart;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * @author moji
 * @version ChartService.java, v 0.1 2023年06月09日 15:28 moji Exp $
 * @date 2023/06/09
 */
public interface ChartService {
    /**
     * 保存报表
     *
     * @param param
     * @return
     */
    DataResult<Long> createWithPermission(ChartCreateParam param);

    /**
     * 更新报表
     *
     * @param param
     * @return
     */
    ActionResult updateWithPermission(ChartUpdateParam param);

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    DataResult<Chart> find(@NotNull Long id);

    /**
     * 查询一条数据
     *
     * @param param
     * @return
     */
    DataResult<Chart> queryExistent(@NotNull ChartQueryParam param);

    /**
     * 查询一条数据
     *
     * @param id
     * @return
     */
    DataResult<Chart> queryExistent(@NotNull Long id);

    /**
     * 查询多条数据
     *
     * @param param
     * @return
     */
    ListResult<Chart> listQuery(@NotNull ChartListQueryParam param);

    /**
     * 通过ID查询图表列表
     *
     * @param ids
     * @return
     */
    ListResult<Chart> queryByIds(@NotEmpty List<Long> ids);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    ActionResult deleteWithPermission(@NotNull Long id);

}
