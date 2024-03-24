package ai.chat2db.server.web.api.controller.rdb.converter;

import ai.chat2db.server.domain.api.param.datasource.DatabaseExportDataParam;
import ai.chat2db.server.domain.api.param.datasource.DatabaseExportParam;
import ai.chat2db.server.web.api.controller.rdb.request.DatabaseCreateRequest;
import ai.chat2db.server.web.api.controller.rdb.request.DatabaseExportDataRequest;
import ai.chat2db.server.web.api.controller.rdb.request.DatabaseExportRequest;
import ai.chat2db.spi.model.Database;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class DatabaseConverter {

    public abstract Database request2param(DatabaseCreateRequest request);

    public abstract DatabaseExportParam request2param(DatabaseExportRequest request);

    public abstract DatabaseExportDataParam request2param(DatabaseExportDataRequest request);
}
