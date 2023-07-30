package ai.chat2db.server.admin.api.controller.datasource.converter;

import ai.chat2db.server.admin.api.controller.datasource.request.DataSourceCreateRequest;
import ai.chat2db.server.admin.api.controller.datasource.request.DataSourcePageQueryRequest;
import ai.chat2db.server.admin.api.controller.datasource.request.DataSourceUpdateRequest;
import ai.chat2db.server.admin.api.controller.datasource.vo.DataSourcePageQueryVO;
import ai.chat2db.server.domain.api.model.DataSource;
import ai.chat2db.server.domain.api.param.DataSourceCreateParam;
import ai.chat2db.server.domain.api.param.DataSourcePageQueryParam;
import ai.chat2db.server.domain.api.param.DataSourceUpdateParam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * converter
 *
 * @author Jiaju Zhuang
 */
@Mapper(componentModel = "spring")
public abstract class DataSourceAdminConverter {

    /**
     * conversion
     *
     * @param request
     * @return
     */
    public abstract DataSourcePageQueryParam request2param(DataSourcePageQueryRequest request);

    /**
     * conversion
     *
     * @param dto
     * @return
     */
    public abstract DataSourcePageQueryVO dto2vo(DataSource dto);

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
}
