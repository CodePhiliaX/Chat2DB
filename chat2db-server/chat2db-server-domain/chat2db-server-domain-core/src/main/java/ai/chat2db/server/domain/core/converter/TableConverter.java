package ai.chat2db.server.domain.core.converter;

import ai.chat2db.server.domain.api.param.TableVectorParam;
import ai.chat2db.server.domain.repository.entity.TableVectorMappingDO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class TableConverter {

    /**
     * TableVectorParam to TableVectorMappingDO
     *
     * @param param
     * @return
     */
    public abstract TableVectorMappingDO toTableVectorMappingDO(TableVectorParam param);
}
