package com.alibaba.dbhub.server.web.api.controller.data.source.converter;

import java.util.List;

import com.alibaba.dbhub.server.domain.api.model.DataSource;
import com.alibaba.dbhub.server.domain.api.param.DataSourceCreateParam;
import com.alibaba.dbhub.server.domain.api.param.DataSourcePreConnectParam;
import com.alibaba.dbhub.server.domain.support.model.Database;
import com.alibaba.dbhub.server.domain.api.param.ConsoleCloseParam;
import com.alibaba.dbhub.server.domain.api.param.ConsoleConnectParam;
import com.alibaba.dbhub.server.domain.api.param.DataSourcePageQueryParam;
import com.alibaba.dbhub.server.domain.api.param.DataSourceUpdateParam;
import com.alibaba.dbhub.server.web.api.controller.data.source.request.ConsoleCloseRequest;
import com.alibaba.dbhub.server.web.api.controller.data.source.request.ConsoleConnectRequest;
import com.alibaba.dbhub.server.web.api.controller.data.source.request.DataSourceCreateRequest;
import com.alibaba.dbhub.server.web.api.controller.data.source.request.DataSourceQueryRequest;
import com.alibaba.dbhub.server.web.api.controller.data.source.request.DataSourceTestRequest;
import com.alibaba.dbhub.server.web.api.controller.data.source.request.DataSourceUpdateRequest;
import com.alibaba.dbhub.server.web.api.controller.data.source.vo.DataSourceVO;
import com.alibaba.dbhub.server.web.api.controller.data.source.vo.DatabaseVO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author moji
 * @version DataSourceWebConverter.java, v 0.1 2022年09月23日 16:45 moji Exp $
 * @date 2022/09/23
 */
@Mapper(componentModel = "spring")
public abstract class DataSourceWebConverter {

    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    @Mappings({
        @Mapping(source = "user", target = "userName")
    })
    public abstract DataSourceCreateParam createReq2param(DataSourceCreateRequest request);

    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    @Mappings({
        @Mapping(source = "user", target = "userName")
    })
    public abstract DataSourceUpdateParam updateReq2param(DataSourceUpdateRequest request);

    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract DataSourcePageQueryParam queryReq2param(DataSourceQueryRequest request);

    /**
     * 模型转换
     *
     * @param dataSource
     * @return
     */
    @Mappings({
        @Mapping(target = "user", source = "userName")
    })
    public abstract DataSourceVO dto2vo(DataSource dataSource);

    /**
     * 模型转换
     *
     * @param dataSources
     * @return
     */
    public abstract List<DataSourceVO> dto2vo(List<DataSource> dataSources);

    /**
     * 模型转换
     *
     * @param databaseDTO
     * @return
     */
    public abstract DatabaseVO databaseDto2vo(Database databaseDTO);

    /**
     * 模型转换
     *
     * @param databaseDTOS
     * @return
     */
    public abstract List<DatabaseVO> databaseDto2vo(List<Database> databaseDTOS);

    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract DataSourcePreConnectParam testRequest2param(DataSourceTestRequest request);

    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract ConsoleConnectParam request2connectParam(ConsoleConnectRequest request);

    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract ConsoleCloseParam request2closeParam(ConsoleCloseRequest request);
}
