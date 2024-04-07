package ai.chat2db.server.domain.core.converter;

import java.util.List;

import ai.chat2db.server.domain.api.model.Operation;
import ai.chat2db.server.domain.api.param.operation.OperationSavedParam;
import ai.chat2db.server.domain.api.param.operation.OperationUpdateParam;
import ai.chat2db.server.domain.repository.entity.OperationSavedDO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author moji
 * @version UserSavedDdlCoreConverter.java, v 0.1 September 25, 2022 15:50 moji Exp $
 * @date 2022/09/25
 */
@Mapper(componentModel = "spring")
public abstract class OperationConverter {

    /**
     * Parameter conversion
     *
     * @param param
     * @return
     */
    @Mappings({
        @Mapping(source = "schemaName", target = "dbSchemaName")
    })
    public abstract OperationSavedDO param2do(OperationSavedParam param);

    /**
     * Parameter conversion
     *
     * @param param
     * @return
     */
    @Mappings({
        @Mapping(source = "schemaName", target = "dbSchemaName")
    })
    public abstract OperationSavedDO param2do(OperationUpdateParam param);

    /**
     * Model conversion
     *
     * @param userSavedDdlDO
     * @return
     */
    @Mappings({
        @Mapping(source = "dbSchemaName", target = "schemaName")
    })
    public abstract Operation do2dto(OperationSavedDO userSavedDdlDO);

    /**
     * Model conversion
     *
     * @param userSavedDdlDOS
     * @return
     */
    public abstract List<Operation> do2dto(List<OperationSavedDO> userSavedDdlDOS);
}
