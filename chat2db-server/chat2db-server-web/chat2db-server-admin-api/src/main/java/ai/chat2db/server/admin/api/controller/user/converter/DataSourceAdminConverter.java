package ai.chat2db.server.admin.api.controller.user.converter;

import ai.chat2db.server.admin.api.controller.common.request.CommonPageQueryRequest;
import ai.chat2db.server.admin.api.controller.user.request.DataSourceUpdateRequest;
import ai.chat2db.server.admin.api.controller.user.request.UserCreateRequest;
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
    public abstract DataSourcePageQueryParam request2param(CommonPageQueryRequest request);

    /**
     * conversion
     *
     * @param request
     * @return
     */
    public abstract DataSourcePageQueryParam request2paramAccess(CommonPageQueryRequest request);

    ///**
    // * conversion
    // *
    // * @param dto
    // * @return
    // */
    //public abstract DataSourcePageQueryVO dto2vo(DataSource dto);

    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    @Mappings({
        @Mapping(source = "user", target = "userName")
    })
    public abstract DataSourceCreateParam createReq2param(UserCreateRequest request);

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
