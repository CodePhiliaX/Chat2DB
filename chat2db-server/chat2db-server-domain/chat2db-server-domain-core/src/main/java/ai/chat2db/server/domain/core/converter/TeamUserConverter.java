package ai.chat2db.server.domain.core.converter;

import java.util.List;
import java.util.Map;

import ai.chat2db.server.domain.api.model.Environment;
import ai.chat2db.server.domain.api.model.TeamUser;
import ai.chat2db.server.domain.api.param.team.user.TeamUserCreatParam;
import ai.chat2db.server.domain.api.service.EnvironmentService;
import ai.chat2db.server.domain.repository.entity.TeamUserDO;
import ai.chat2db.server.tools.common.util.EasyCollectionUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.context.annotation.Lazy;

/**
 * converter
 *
 * @author Jiaju Zhuang
 */
@Slf4j
@Mapper(componentModel = "spring")
public abstract class TeamUserConverter {

    @Resource
    @Lazy
    private EnvironmentService environmentService;

    /**
     * convert
     *
     * @param data
     * @return
     */
    @Mappings({
        @Mapping(target = "team.id", source = "teamId"),
        @Mapping(target = "user.id", source = "userId"),
    })
    public abstract TeamUser do2dto(TeamUserDO data);

    /**
     * convert
     *
     * @param list
     * @return
     */
    public abstract List<TeamUser> do2dto(List<TeamUserDO> list);

    /**
     * convert
     *
     * @param param

     * @return
     */
    @Mappings({
        @Mapping(target = "createUserId", source = "userId"),
        @Mapping(target = "modifiedUserId", source = "userId"),
    })
    public abstract TeamUserDO param2do(TeamUserCreatParam param, Long userId);


    /**
     * Fill in detailed information
     *
     * @param list
     */
    public void fillDetail(List<Environment> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<Long> idList = EasyCollectionUtils.toList(list, Environment::getId);
        List<Environment> queryList = environmentService.listQuery(idList).getData();
        Map<Long, Environment> queryMap = EasyCollectionUtils.toIdentityMap(queryList, Environment::getId);
        for (Environment data : list) {
            if (data == null || data.getId() == null) {
                continue;
            }
            Environment query = queryMap.get(data.getId());
            add(data, query);
        }
    }

    @Mappings({
        @Mapping(target = "id", ignore = true),
    })
    public abstract void add(@MappingTarget Environment target, Environment source);
}
