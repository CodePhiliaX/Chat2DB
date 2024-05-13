package ai.chat2db.server.web.api.controller.data.source.converter;

import java.util.List;

import ai.chat2db.server.domain.api.enums.DataSourceKindEnum;
import ai.chat2db.server.domain.api.model.DataSource;
import ai.chat2db.server.domain.api.param.ConsoleCloseParam;
import ai.chat2db.server.domain.api.param.ConsoleConnectParam;
import ai.chat2db.server.domain.api.param.datasource.DataSourceCreateParam;
import ai.chat2db.server.domain.api.param.datasource.DataSourcePageQueryParam;
import ai.chat2db.server.domain.api.param.datasource.DataSourcePreConnectParam;
import ai.chat2db.server.domain.api.param.datasource.DataSourceUpdateParam;
import ai.chat2db.server.web.api.controller.data.source.request.ConsoleCloseRequest;
import ai.chat2db.server.web.api.controller.data.source.request.ConsoleConnectRequest;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceCreateRequest;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceQueryRequest;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceTestRequest;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceUpdateRequest;
import ai.chat2db.server.web.api.controller.data.source.vo.DataSourceVO;
import ai.chat2db.server.web.api.controller.data.source.vo.DatabaseVO;
import ai.chat2db.spi.model.Database;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author moji
 * @version DataSourceWebConverter.java, v 0.1 September 23, 2022 16:45 moji Exp $
 * @date 2022/09/23
 */
@Mapper(componentModel = "spring", imports = {DataSourceKindEnum.class})
public abstract class DataSourceWebConverter {

    /**
     * Parameter conversion
     *
     * @param request
     * @return
     */
    @Mappings({
        @Mapping(source = "user", target = "userName"),
        @Mapping(target = "kind", expression = "java(DataSourceKindEnum.PRIVATE.getCode())")
    })
    public abstract DataSourceCreateParam createReq2param(DataSourceCreateRequest request);

    /**
     * Parameter conversion
     *
     * @param request
     * @return
     */
    @Mappings({
        @Mapping(source = "user", target = "userName")
    })
    public abstract DataSourceUpdateParam updateReq2param(DataSourceUpdateRequest request);

    /**
     * Parameter conversion
     *
     * @param request
     * @return
     */
    public abstract DataSourcePageQueryParam queryReq2param(DataSourceQueryRequest request);

    /**
     * Model conversion
     *
     * @param dataSource
     * @return
     */
    @Mappings({
        @Mapping(target = "user", source = "userName")
    })
    public abstract DataSourceVO dto2vo(DataSource dataSource);

    /**
     * Model conversion
     *
     * @param dataSources
     * @return
     */
    public abstract List<DataSourceVO> dto2vo(List<DataSource> dataSources);

    /**
     * Model conversion
     *
     * @param databaseDTO
     * @return
     */
    public abstract DatabaseVO databaseDto2vo(Database databaseDTO);

    /**
     * Model conversion
     *
     * @param databaseDTOS
     * @return
     */
    public abstract List<DatabaseVO> databaseDto2vo(List<Database> databaseDTOS);

    /**
     * Parameter conversion
     *
     * @param request
     * @return
     */
    public abstract DataSourcePreConnectParam testRequest2param(DataSourceTestRequest request);

    /**
     * Parameter conversion
     *
     * @param request
     * @return
     */
    public abstract ConsoleConnectParam request2connectParam(ConsoleConnectRequest request);

    /**
     * Parameter conversion
     *
     * @param request
     * @return
     */
    public abstract ConsoleCloseParam request2closeParam(ConsoleCloseRequest request);
}
