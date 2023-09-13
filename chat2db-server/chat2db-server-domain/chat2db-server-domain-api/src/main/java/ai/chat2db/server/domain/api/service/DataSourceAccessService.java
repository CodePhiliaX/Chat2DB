package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.model.DataSourceAccess;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessComprehensivePageQueryParam;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessCreatParam;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessPageQueryParam;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessSelector;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import jakarta.validation.constraints.NotNull;

/**
 * Data Source Access
 *
 * @author Jiaju Zhuang
 */
public interface DataSourceAccessService {

    /**
     * Comprehensive Paging Query Data
     *
     * @param param
     * @param selector
     * @return
     */
    PageResult<DataSourceAccess> pageQuery(DataSourceAccessPageQueryParam param, DataSourceAccessSelector selector);

    /**
     * Paging Query Data
     *
     * @param param
     * @param selector
     * @return
     */
    PageResult<DataSourceAccess> comprehensivePageQuery(DataSourceAccessComprehensivePageQueryParam param,
        DataSourceAccessSelector selector);


    /**
     * Batch Create
     *
     * @param param
     * @return
     */
    DataResult<Long> create(DataSourceAccessCreatParam param);
    /**
     * delete
     *
     * @param id
     * @return
     */
    ActionResult delete(@NotNull Long id);
}
