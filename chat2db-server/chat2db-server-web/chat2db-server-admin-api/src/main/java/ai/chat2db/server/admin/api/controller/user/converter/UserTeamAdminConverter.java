package ai.chat2db.server.admin.api.controller.user.converter;

import ai.chat2db.server.admin.api.controller.user.request.UserPageCommonQueryRequest;
import ai.chat2db.server.admin.api.controller.user.vo.UserTeamPageQueryVO;
import ai.chat2db.server.domain.api.model.TeamUser;
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
public abstract class UserTeamAdminConverter {

    /**
     * convert
     *
     * @param request
     * @return
     */
    @Mappings({
        @Mapping(source = "searchKey", target = "teamSearchKey"),
        @Mapping(target = "enableReturnCount", expression = "java(true)"),
    })
    public abstract TeamUserComprehensivePageQueryParam request2param(UserPageCommonQueryRequest request);

    /**
     * conversion
     *
     * @param dto
     * @return
     */
    public abstract UserTeamPageQueryVO dto2vo(TeamUser dto);
}
