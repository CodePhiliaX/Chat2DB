package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.model.DataSourceAccess;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessComprehensivePageQueryParam;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessCreatParam;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessPageQueryParam;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessSelector;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.base.wrapper.ServicePage;
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
    ServicePage<DataSourceAccess> pageQuery(DataSourceAccessPageQueryParam param, DataSourceAccessSelector selector);

    /**
     * Paging Query Data
     *
     * @param param
     * @param selector
     * @return
     */
    ServicePage<DataSourceAccess> comprehensivePageQuery(DataSourceAccessComprehensivePageQueryParam param,
        DataSourceAccessSelector selector);


    /**
     * Batch Create
     *
     * @param param
     * @return
     */
    Long create(DataSourceAccessCreatParam param);
    /**
     * delete
     *
     * @param id
     * @return
     */
    void delete(@NotNull Long id);
}
