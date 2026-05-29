package ai.chat2db.server.web.api.controller.rdb.converter;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import ai.chat2db.server.domain.api.param.DlCountParam;
import ai.chat2db.server.domain.api.param.DeprecatedTableParam;
import ai.chat2db.server.domain.api.param.DlExecuteParam;
import ai.chat2db.server.domain.api.param.DropKeyParam;
import ai.chat2db.server.domain.api.param.DropParam;
import ai.chat2db.server.domain.api.param.OrderByParam;
import ai.chat2db.server.domain.api.param.SchemaQueryParam;
import ai.chat2db.server.domain.api.param.ShowCreateTableParam;
import ai.chat2db.server.domain.api.param.TablePageQueryParam;
import ai.chat2db.server.domain.api.param.TableQueryParam;
import ai.chat2db.server.domain.api.param.TableVectorParam;
import ai.chat2db.server.domain.api.param.UpdateSelectResultParam;
import ai.chat2db.server.web.api.controller.ai.request.ChatQueryRequest;
import ai.chat2db.server.web.api.controller.data.source.vo.DatabaseVO;
import ai.chat2db.server.web.api.controller.rdb.request.DataExportRequest;
import ai.chat2db.server.web.api.controller.rdb.request.ColumnRequest;
import ai.chat2db.server.web.api.controller.rdb.request.DataExportRequest;
import ai.chat2db.server.web.api.controller.rdb.request.DatabaseCreateRequest;
import ai.chat2db.server.web.api.controller.rdb.request.DdlCountRequest;
import ai.chat2db.server.web.api.controller.rdb.request.DdlExportRequest;
import ai.chat2db.server.web.api.controller.rdb.request.DdlRequest;
import ai.chat2db.server.web.api.controller.rdb.request.DeprecatedTableRequest;
import ai.chat2db.server.web.api.controller.rdb.request.DmlRequest;
import ai.chat2db.server.web.api.controller.rdb.request.DmlTableRequest;
import ai.chat2db.server.web.api.controller.rdb.request.KeyDeleteRequest;
import ai.chat2db.server.web.api.controller.rdb.request.OrderByRequest;
import ai.chat2db.server.web.api.controller.rdb.request.SelectResultUpdateRequest;
import ai.chat2db.server.web.api.controller.rdb.request.TableBriefQueryRequest;
import ai.chat2db.server.web.api.controller.rdb.request.TableDeleteRequest;
import ai.chat2db.server.web.api.controller.rdb.request.TableDetailQueryRequest;
import ai.chat2db.server.web.api.controller.rdb.request.TableMilvusQueryRequest;
import ai.chat2db.server.web.api.controller.rdb.request.TableRequest;
import ai.chat2db.server.web.api.controller.rdb.vo.ColumnVO;
import ai.chat2db.server.web.api.controller.rdb.vo.ExecuteResultVO;
import ai.chat2db.server.web.api.controller.rdb.vo.IndexVO;
import ai.chat2db.server.web.api.controller.rdb.vo.MetaSchemaVO;
import ai.chat2db.server.web.api.controller.rdb.vo.SchemaVO;
import ai.chat2db.server.web.api.controller.rdb.vo.SqlVO;
import ai.chat2db.server.web.api.controller.rdb.vo.TableVO;
import ai.chat2db.server.web.api.http.request.EsTableSchemaRequest;
import ai.chat2db.spi.model.Database;
import ai.chat2db.spi.model.ExecuteResult;
import ai.chat2db.spi.model.MetaSchema;
import ai.chat2db.spi.model.Schema;
import ai.chat2db.spi.model.Sql;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.model.TableIndex;

/**
 * @author moji
 * @version MysqlDataConverter.java, v 0.1 2022年10月14日 14:04 moji Exp $
 * @date 2022/10/14
 */
@Mapper(componentModel = "spring")
public abstract class RdbWebConverter {

    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract DlExecuteParam request2param(DmlRequest request);


    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract OrderByParam request2param(OrderByRequest request);

    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract DlExecuteParam request2param(DmlTableRequest request);

    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract DlExecuteParam tableManageRequest2param(DdlRequest request);


    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract DlCountParam request2param(DdlCountRequest request);

    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract TableQueryParam tableRequest2param(TableDetailQueryRequest request);


    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract Table tableRequest2param(TableRequest request);

    /**
     * 参数转换
     *
     * @param dto
     * @return
     */
    public abstract SqlVO dto2vo(Sql dto);

    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract TablePageQueryParam chatQueryRequest2page(ChatQueryRequest request);
    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract TablePageQueryParam tablePageRequest2param(TableBriefQueryRequest request);
    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract TablePageQueryParam tablePageRequest2param(DataExportRequest request);
    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract TableQueryParam tableRequest2param(DataExportRequest request);

    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract ShowCreateTableParam ddlExport2showCreate(DdlExportRequest request);

    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract DropParam tableDelete2dropParam(TableDeleteRequest request);
    /**
     * 参数转换
     * @param request
     * @return
     */
    public abstract DropKeyParam keyDelete2dropParm(KeyDeleteRequest request);


    /**
     * 模型转换
     *
     * @param dto
     * @return
     */
    public abstract ExecuteResultVO dto2vo(ExecuteResult dto);

    /**
     * 模型转换
     *
     * @param dtos
     * @return
     */
    public abstract List<ExecuteResultVO> dto2vo(List<ExecuteResult> dtos);

    /**
     * 模型转换
     *
     * @param dto
     * @return
     */
    @Mappings({
        @Mapping(target = "comment", expression = "java(getComment(dto))")
    })
    public abstract ColumnVO columnDto2vo(TableColumn dto);

    /**
     * 模型转换
     *
     * @param dtos
     * @return
     */
    public abstract List<ColumnVO> columnDto2vo(List<TableColumn> dtos);

    // 自定义方法，获取非空的comment
    protected String getComment(TableColumn dto) {
        return StringUtils.defaultIfBlank(dto.getComment(), dto.getAiComment());
    }
    /**
     * 模型转换
     *
     * @param dto
     * @return
     */
    @Mappings({
        @Mapping(source = "columnList", target = "columnList")
    })
    public abstract IndexVO indexDto2vo(TableIndex dto);

    /**
     * 模型转换
     *
     * @param dtos
     * @return
     */
    public abstract List<IndexVO> indexDto2vo(List<TableIndex> dtos);

    /**
     * 模型转换
     *
     * @param dto
     * @return
     */
    @Mappings({
        @Mapping(source = "columnList", target = "columnList"),
        @Mapping(source = "indexList", target = "indexList"),
        @Mapping(source = "comment", target = "rawComment"),
        @Mapping(source = "aiComment", target = "aiComment"),
        @Mapping(target = "comment", expression = "java(getComment(dto))")
    })
    public abstract TableVO tableDto2vo(Table dto);

    /**
     * 模型转换
     *
     * @param dtos
     * @return
     */
    public abstract List<TableVO> tableDto2vo(List<Table> dtos);

    /**
     * 模型转换
     * @param tableColumns
     * @return
     */
    public abstract List<SchemaVO> schemaDto2vo(List<Schema> tableColumns);


    // 自定义方法，获取非空的comment
    protected String getComment(Table dto) {
        return StringUtils.defaultIfBlank(dto.getComment(), dto.getAiComment());
    }

    /**
     * 模型转换
     * @param dto
     * @return
     */
    public abstract SchemaVO schemaDto2vo(Schema dto);

    /**
     * 模型转换
     * @param dto
     * @return
     */
    public abstract DatabaseVO databaseDto2vo(Database dto);


    /**
     * 模型转换
     * @param dto
     * @return
     */
    public abstract List<DatabaseVO> databaseDto2vo(List<Database> dto);

    public abstract MetaSchemaVO metaSchemaDto2vo(MetaSchema data);


    public abstract UpdateSelectResultParam request2param(SelectResultUpdateRequest request);

    public abstract TableMilvusQueryRequest request2request(TableBriefQueryRequest request);

    @Mappings({
            @Mapping(source = "databaseName", target = "database"),
            @Mapping(source = "schemaName", target = "schema"),
    })
    public abstract TableVectorParam param2param(TableBriefQueryRequest request);

    public abstract EsTableSchemaRequest req2req(TableBriefQueryRequest request);

    public abstract TablePageQueryParam schemaReq2page(EsTableSchemaRequest request);
    public abstract SchemaQueryParam chatQueryRequest2schemaParam(ChatQueryRequest queryRequest);

    public abstract TableQueryParam chatQueryRequest2Param(ChatQueryRequest queryRequest);

    public abstract DeprecatedTableParam deprecatedTableRequest2param(DeprecatedTableRequest request);
}
