package ai.chat2db.server.admin.api.controller.common.converter;

import ai.chat2db.server.admin.api.controller.common.vo.TeamUserListVO;
import ai.chat2db.server.admin.api.controller.datasource.vo.SimpleDataSourceVO;
import ai.chat2db.server.admin.api.controller.team.vo.SimpleTeamVO;
import ai.chat2db.server.admin.api.controller.user.vo.SimpleUserVO;
import ai.chat2db.server.common.api.controller.request.CommonQueryRequest;
import ai.chat2db.server.domain.api.enums.AccessObjectTypeEnum;
import ai.chat2db.server.domain.api.enums.DataSourceKindEnum;
import ai.chat2db.server.domain.api.model.DataSource;
import ai.chat2db.server.domain.api.model.Team;
import ai.chat2db.server.domain.api.model.User;
import ai.chat2db.server.domain.api.param.datasource.DataSourcePageQueryParam;
import ai.chat2db.server.domain.api.param.team.TeamPageQueryParam;
import ai.chat2db.server.domain.api.param.user.UserPageQueryParam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * converter
 *
 * @author Jiaju Zhuang
 */
@Mapper(componentModel = "spring", imports = {AccessObjectTypeEnum.class, DataSourceKindEnum.class})
public abstract class CommonAdminConverter {

    /**
     * conversion
     *
     * @param request
     * @return
     */
    @Mappings({
        @Mapping(target = "pageSize", expression = "java(10)"),
    })
    public abstract TeamPageQueryParam request2paramTeam(CommonQueryRequest request);

    /**
     * conversion
     *
     * @param request
     * @return
     */
    @Mappings({
        @Mapping(target = "pageSize", expression = "java(10)"),
    })
    public abstract UserPageQueryParam request2paramUser(CommonQueryRequest request);

    /**
     * conversion
     *
     * @param request
     * @return
     */
    @Mappings({
        @Mapping(target = "pageSize", expression = "java(10)"),
        @Mapping(target = "kind", expression = "java(DataSourceKindEnum.SHARED.getCode())"),
    })
    public abstract DataSourcePageQueryParam request2paramDataSource(CommonQueryRequest request);

    /**
     * conversion
     *
     * @param dto
     * @return
     */
    public abstract SimpleTeamVO dto2voTeam(Team dto);

    /**
     * conversion
     *
     * @param dto
     * @return
     */
    public abstract SimpleDataSourceVO dto2voDataSource(DataSource dto);

    /**
     * conversion
     *
     * @param dto
     * @return
     */
    public abstract SimpleUserVO dto2voUser(User dto);

    /**
     * conversion
     *
     * @param dto
     * @return
     */
    @Mappings({
        @Mapping(target = "type", expression = "java(AccessObjectTypeEnum.TEAM.getCode())"),
        @Mapping(target = "code", source = "code"),
        @Mapping(target = "name", source = "name"),
    })
    public abstract TeamUserListVO dto2voTeamUser(Team dto);

    /**
     * conversion
     *
     * @param dto
     * @return
     */
    @Mappings({
        @Mapping(target = "type", expression = "java(AccessObjectTypeEnum.USER.getCode())"),
        @Mapping(target = "code", source = "userName"),
        @Mapping(target = "name", source = "nickName"),
    })
    public abstract TeamUserListVO dto2voTeamUser(User dto);
}
