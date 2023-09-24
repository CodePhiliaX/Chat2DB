package ai.chat2db.server.admin.api.controller.datasource.converter;

import ai.chat2db.server.admin.api.controller.datasource.request.DataSourceCreateRequest;
import ai.chat2db.server.admin.api.controller.datasource.request.DataSourceUpdateRequest;
import ai.chat2db.server.admin.api.controller.datasource.vo.DataSourcePageQueryVO;
import ai.chat2db.server.common.api.controller.request.CommonPageQueryRequest;
import ai.chat2db.server.domain.api.enums.DataSourceKindEnum;
import ai.chat2db.server.domain.api.model.DataSource;
import ai.chat2db.server.domain.api.param.datasource.DataSourceCreateParam;
import ai.chat2db.server.domain.api.param.datasource.DataSourcePageQueryParam;
import ai.chat2db.server.domain.api.param.datasource.DataSourceUpdateParam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * converter
 *
 * @author Jiaju Zhuang
 */
@Mapper(componentModel = "spring",imports = {DataSourceKindEnum.class})
public abstract class DataSourceAdminConverter {

    /**
     * conversion
     *
     * @param request
     * @return
     */
    @Mappings({
        @Mapping(target = "enableReturnCount", expression = "java(true)"),
        @Mapping(target = "kind", expression = "java(DataSourceKindEnum.SHARED.getCode())"),
    })
    public abstract DataSourcePageQueryParam request2param(CommonPageQueryRequest request);

    /**
     * conversion
     *
     * @param request
     * @return
     */
    @Mappings({
        @Mapping(target = "enableReturnCount", expression = "java(true)"),
    })
    public abstract DataSourcePageQueryParam request2paramAccess(CommonPageQueryRequest request);

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
        @Mapping(source = "user", target = "userName"),
        @Mapping(target = "kind", expression = "java(DataSourceKindEnum.SHARED.getCode())"),
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
