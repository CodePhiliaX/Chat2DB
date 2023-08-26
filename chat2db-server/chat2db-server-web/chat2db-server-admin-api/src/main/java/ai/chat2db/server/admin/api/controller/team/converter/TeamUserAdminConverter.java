package ai.chat2db.server.admin.api.controller.team.converter;

import ai.chat2db.server.admin.api.controller.datasource.request.DataSourceAccessBatchCreateRequest;
import ai.chat2db.server.admin.api.controller.team.request.TeamPageCommonQueryRequest;
import ai.chat2db.server.admin.api.controller.team.vo.TeamUserPageQueryVO;
import ai.chat2db.server.domain.api.model.TeamUser;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessBatchCreatParam;
import ai.chat2db.server.domain.api.param.team.user.TeamUserComprehensivePageQueryParam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * converter
 *
 * @author Jiaju Zhuang
 */
@Mapper(componentModel = "spring")
public abstract class TeamUserAdminConverter {

    /**
     * convert
     *
     * @param request
     * @return
     */
    @Mappings({
        @Mapping(source = "searchKey", target = "userSearchKey"),
        @Mapping(target = "enableReturnCount", expression = "java(true)"),
    })
    public abstract TeamUserComprehensivePageQueryParam request2param(TeamPageCommonQueryRequest request);

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
    public abstract TeamUserPageQueryVO dto2vo(TeamUser dto);

}
