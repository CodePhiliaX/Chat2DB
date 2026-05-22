package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.model.schemaDiff.SchemaDiffResult;
import ai.chat2db.server.domain.api.param.schemaDiff.SchemaCompareParam;
import ai.chat2db.server.domain.api.param.schemaDiff.SchemaMigrateParam;
import ai.chat2db.server.domain.api.param.schemaDiff.MigrateResult;

public interface SchemaDiffService {

    SchemaDiffResult compare(SchemaCompareParam param);

    MigrateResult migrate(SchemaMigrateParam param);
}
