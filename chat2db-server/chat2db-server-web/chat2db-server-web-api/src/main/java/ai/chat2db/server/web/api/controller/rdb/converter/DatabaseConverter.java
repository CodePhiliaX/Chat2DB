package ai.chat2db.server.web.api.controller.rdb.converter;

import ai.chat2db.server.web.api.controller.rdb.request.DatabaseCreateRequest;
import ai.chat2db.spi.model.Database;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class DatabaseConverter {

    public abstract Database request2param(DatabaseCreateRequest request);
}
