package ai.chat2db.server.web.api.controller.rdb.converter;

import ai.chat2db.server.web.api.controller.rdb.request.ProcedureUpdateRequest;
import ai.chat2db.spi.model.Procedure;
import org.mapstruct.Mapper;

/**
 * @author: zgq
 * @date: 2024年02月24日 13:39
 */
@Mapper(componentModel = "spring")
public abstract class ProcedureConverter {

    public abstract Procedure request2param(ProcedureUpdateRequest request);
}
