package ai.chat2db.server.domain.api.service;

import java.util.List;

import ai.chat2db.server.domain.api.param.DlCountParam;
import ai.chat2db.server.domain.api.param.DlExecuteParam;
import ai.chat2db.server.domain.api.param.OrderByParam;
import ai.chat2db.server.domain.api.param.UpdateSelectResultParam;
import ai.chat2db.spi.model.ExecuteResult;


/**
 * 数据源管理服务
 *
 * @author moji
 * @version DataSourceCoreService.java, v 0.1 2022年09月23日 15:22 moji Exp $
 * @date 2022/09/23
 */
public interface DlTemplateService {

    /**
     * 数据源执行dl
     *
     * @param param
     * @return
     */
    List<ExecuteResult> execute(DlExecuteParam param);


    /**
     * 数据源执行update
     *
     * @param param
     * @return
     */
    ExecuteResult executeUpdate(DlExecuteParam param);

    /**
     * 执行统计sql
     *
     * @param param
     * @return
     */
    Long count(DlCountParam param);


    /**
     * 更新查询结果
     * @param param
     * @return
     */
    String updateSelectResult(UpdateSelectResultParam param);


    /**
     *
     * @param param
     * @return
     */
    String getOrderBySql(OrderByParam param);

}
