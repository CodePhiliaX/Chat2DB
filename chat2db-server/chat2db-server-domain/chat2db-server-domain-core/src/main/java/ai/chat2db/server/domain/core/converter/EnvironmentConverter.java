package ai.chat2db.server.domain.core.converter;

import java.util.List;
import java.util.Map;

import ai.chat2db.server.domain.api.model.Environment;
import ai.chat2db.server.domain.api.service.EnvironmentService;
import ai.chat2db.server.domain.repository.entity.EnvironmentDO;
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
public abstract class EnvironmentConverter {

    @Resource
    @Lazy
    private EnvironmentService environmentService;

    /**
     * convert
     *
     * @param list
     * @return
     */
    public abstract List<Environment> do2dto(List<EnvironmentDO> list);

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
