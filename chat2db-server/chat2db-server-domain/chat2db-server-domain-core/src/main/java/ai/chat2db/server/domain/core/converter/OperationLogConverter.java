package ai.chat2db.server.domain.core.converter;

import java.util.List;

import ai.chat2db.server.domain.api.model.OperationLog;
import ai.chat2db.server.domain.api.param.operation.OperationLogCreateParam;
import ai.chat2db.server.domain.repository.entity.OperationLogDO;

import org.mapstruct.Mapper;

/**
 * @author moji
 * @version UserExecutedDdlCoreConverter.java, v 0.1 September 25, 2022 14:08 moji Exp $
 * @date 2022/09/25
 */
@Mapper(componentModel = "spring")
public abstract class OperationLogConverter {

    /**
     * Parameter conversion
     *
     * @param param
     * @return
     */
    public abstract OperationLogDO param2do(OperationLogCreateParam param);

    /**
     * Model conversion
     *
     * @param userExecutedDdlDO
     * @return
     */
    public abstract OperationLog do2dto(OperationLogDO userExecutedDdlDO);

    /**
     * Model conversion
     *
     * @param userExecutedDdlDOS
     * @return
     */
    public abstract List<OperationLog> do2dto(List<OperationLogDO> userExecutedDdlDOS);
}
