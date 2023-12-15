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
 * @version UserSavedDdlCoreConverter.java, v 0.1 2022年09月25日 15:50 moji Exp $
 * @date 2022/09/25
 */
@Mapper(componentModel = "spring")
public abstract class OperationConverter {

    /**
     * 参数转换
     *
     * @param param
     * @return
     */
    @Mappings({
        @Mapping(source = "schemaName", target = "dbSchemaName")
    })
    public abstract OperationSavedDO param2do(OperationSavedParam param);

    /**
     * 参数转换
     *
     * @param param
     * @return
     */
    @Mappings({
        @Mapping(source = "schemaName", target = "dbSchemaName")
    })
    public abstract OperationSavedDO param2do(OperationUpdateParam param);

    /**
     * 模型转换
     *
     * @param userSavedDdlDO
     * @return
     */
    @Mappings({
        @Mapping(source = "dbSchemaName", target = "schemaName")
    })
    public abstract Operation do2dto(OperationSavedDO userSavedDdlDO);

    /**
     * 模型转换
     *
     * @param userSavedDdlDOS
     * @return
     */
    public abstract List<Operation> do2dto(List<OperationSavedDO> userSavedDdlDOS);
}
