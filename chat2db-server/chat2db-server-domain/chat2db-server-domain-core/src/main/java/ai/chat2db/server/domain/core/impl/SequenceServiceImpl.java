package ai.chat2db.server.domain.core.impl;


import ai.chat2db.server.domain.api.param.DropParam;
import ai.chat2db.server.domain.api.param.SequencePageQueryParam;
import ai.chat2db.server.domain.api.param.SequenceQueryParam;
import ai.chat2db.server.domain.api.param.ShowCreateSequenceParam;
import ai.chat2db.server.domain.api.service.SequenceService;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.model.*;
import ai.chat2db.spi.sql.Chat2DBContext;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Sequence source management serviceImpl
 *
 * @author Sylphy
 */
@Slf4j
@Service
public class SequenceServiceImpl implements SequenceService {
    @Override
    public DataResult<String> showCreateSequence(ShowCreateSequenceParam param) {
        MetaData metaSchema = Chat2DBContext.getMetaData();
        String ddl = metaSchema.sequenceDDL(Chat2DBContext.getConnection(), param.getDatabaseName(), param.getSchemaName(), param.getSequenceName());
        return DataResult.of(ddl);
    }

    @Override
    public ListResult<SimpleSequence> pageQuery(SequencePageQueryParam request) {
        MetaData metaSchema = Chat2DBContext.getMetaData();
        List<SimpleSequence> sequences = metaSchema.sequences(Chat2DBContext.getConnection(), request.getDatabaseName(), request.getSchemaName());
        return ListResult.of(sequences);
    }

    @Override
    public ListResult<Sql> buildSql(Sequence oldSequence, Sequence newSequence) {
        SqlBuilder<?> sqlBuilder = Chat2DBContext.getSqlBuilder();
        List<Sql> sqls = new ArrayList<>();
        if (ObjectUtil.isEmpty(oldSequence)) {
            sqls.add(Sql.builder().sql(sqlBuilder.buildCreateSequenceSql(newSequence)).build());
        } else {
            sqls.add(Sql.builder().sql(sqlBuilder.buildModifySequenceSql(oldSequence, newSequence)).build());
        }
        return ListResult.of(sqls);
    }

    @Override
    public ActionResult drop(DropParam param) {
        DBManage metaSchema = Chat2DBContext.getDBManage();
        metaSchema.dropSequence(Chat2DBContext.getConnection(), param.getDatabaseName(), param.getSchema(), param.getName());
        return ActionResult.isSuccess();
    }

    @Override
    public DataResult<Sequence> query(SequenceQueryParam param){
        MetaData metaSchema = Chat2DBContext.getMetaData();
        Sequence sequences = metaSchema.sequences(Chat2DBContext.getConnection(), param.getDatabaseName(), param.getSchemaName(), param.getSequenceName());
        return DataResult.of(sequences);
    }
}
