package ai.chat2db.server.admin.api.controller.team.converter;

import ai.chat2db.server.admin.api.controller.datasource.request.DataSourceAccessBatchCreateRequest;
import ai.chat2db.server.admin.api.controller.team.vo.TeamDataSourcePageQueryVO;
import ai.chat2db.server.common.api.controller.request.CommonPageQueryRequest;
import ai.chat2db.server.domain.api.enums.DataSourceKindEnum;
import ai.chat2db.server.domain.api.model.DataSourceAccess;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessBatchCreatParam;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessComprehensivePageQueryParam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * converter
 *
 * @author Jiaju Zhuang
 */
@Mapper(componentModel = "spring", imports = {DataSourceKindEnum.class})
public abstract class TeamDataSourcesAdminConverter {

    /**
     * convert
     *
     * @param request
     * @return
     */
    @Mappings({
        @Mapping(source = "searchKey", target = "dataSourceSearchKey"),
    })
    public abstract DataSourceAccessComprehensivePageQueryParam request2param(CommonPageQueryRequest request);

    /**
     * convert
     *
     * @param request
     * @return
     */
    public abstract DataSourceAccessBatchCreatParam request2param(DataSourceAccessBatchCreateRequest request);

    /**
     * conversion
     *
     * @param dto
     * @return
     */
    public abstract TeamDataSourcePageQueryVO dto2vo(DataSourceAccess dto);

}
