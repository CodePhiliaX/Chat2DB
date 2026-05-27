package ai.chat2db.server.domain.api.service;

import java.util.List;

import ai.chat2db.server.domain.api.model.DataSource;
import ai.chat2db.server.domain.api.param.datasource.DataSourceCreateParam;
import ai.chat2db.server.domain.api.param.datasource.DataSourcePageQueryParam;
import ai.chat2db.server.domain.api.param.datasource.DataSourcePreConnectParam;
import ai.chat2db.server.domain.api.param.datasource.DataSourceSelector;
import ai.chat2db.server.domain.api.param.datasource.DataSourceSortUpdateParam;
import ai.chat2db.server.domain.api.param.datasource.DataSourceUpdateParam;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.base.wrapper.ServicePage;
import ai.chat2db.server.tools.common.exception.PermissionDeniedBusinessException;
import ai.chat2db.spi.model.Database;
import jakarta.validation.constraints.NotNull;

/**
 * 数据源管理服务
 *
 * @author moji
 * @version DataSourceCoreService.java, v 0.1 2022年09月23日 15:22 moji Exp $
 * @date 2022/09/23
 */
public interface DataSourceService {

    /**
     * 创建数据源连接
     *
     * @param param
     * @return
     */
    Long createWithPermission(DataSourceCreateParam param);

    /**
     * 更新数据源连接
     *
     * @param param
     * @return
     */
    Long updateWithPermission(DataSourceUpdateParam param);

    /**
     * 删除数据源连接
     *
     * @param id
     * @return
     */
    void deleteWithPermission(@NotNull Long id);

    /**
     * 根据id查询数据源连接详情
     *
     * @param id
     * @return
     */
    DataSource queryById(@NotNull Long id);

    /**
     * 根据id查询数据源连接详情
     *
     * @param id
     * @return
     * @throws ai.chat2db.server.tools.common.exception.DataNotFoundException
     */
    DataSource queryExistent(@NotNull Long id, DataSourceSelector selector);

    /**
     * 克隆连接
     *
     * @param id
     * @return
     */
    Long copyByIdWithPermission(@NotNull Long id);

    /**
     * 更新当前用户的数据源连接排序
     *
     * @param param
     */
    void updateSortWithPermission(DataSourceSortUpdateParam param);

    /**
     * 分页查询数据源列表
     *
     * @param param
     * @param selector
     * @return
     */
    ServicePage<DataSource> queryPage(DataSourcePageQueryParam param, DataSourceSelector selector);

    /**
     * 分页查询数据源列表
     * Need to determine permissions
     *
     * @param param
     * @param selector
     * @return
     * @throws PermissionDeniedBusinessException
     */
    ServicePage<DataSource> queryPageWithPermission(DataSourcePageQueryParam param, DataSourceSelector selector);

    /**
     * 通过 ID 列表查询数据源
     *
     * @param idList
     * @param selector
     * @return
     */
    List<DataSource> listQuery(List<Long> idList, DataSourceSelector selector);

    /**
     * 数据源连接测试
     *
     * @param param
     * @return
     */
    void preConnect(DataSourcePreConnectParam param);

    /**
     * 连接数据源
     *
     * @param id
     * @return
     */
    List<Database> connect(Long id);

    /**
     * 关闭数据源连接
     *
     * @param id
     * @return
     */
    void close(Long id);


    /**
     * 获取数据库类型
     *
     * @param dataSourceId 数据源 ID
     * @return 数据库类型
     */
    String queryDatabaseType(Long dataSourceId);

}
