package ai.chat2db.server.web.api.controller.rdb.converter;

import java.util.List;

import ai.chat2db.server.domain.api.param.DlCountParam;
import ai.chat2db.server.domain.api.param.DlExecuteParam;
import ai.chat2db.server.domain.api.param.DropParam;
import ai.chat2db.server.domain.api.param.ShowCreateTableParam;
import ai.chat2db.server.domain.api.param.TablePageQueryParam;
import ai.chat2db.server.domain.api.param.TableQueryParam;
import ai.chat2db.server.web.api.controller.data.source.vo.DatabaseVO;
import ai.chat2db.server.web.api.controller.rdb.request.DataExportRequest;
import ai.chat2db.server.web.api.controller.rdb.request.DdlCountRequest;
import ai.chat2db.server.web.api.controller.rdb.request.DdlExportRequest;
import ai.chat2db.server.web.api.controller.rdb.request.DdlRequest;
import ai.chat2db.server.web.api.controller.rdb.request.DmlRequest;
import ai.chat2db.server.web.api.controller.rdb.request.TableBriefQueryRequest;
import ai.chat2db.server.web.api.controller.rdb.request.TableDeleteRequest;
import ai.chat2db.server.web.api.controller.rdb.request.TableDetailQueryRequest;
import ai.chat2db.server.web.api.controller.rdb.request.TableRequest;
import ai.chat2db.server.web.api.controller.rdb.vo.ColumnVO;
import ai.chat2db.server.web.api.controller.rdb.vo.ExecuteResultVO;
import ai.chat2db.server.web.api.controller.rdb.vo.IndexVO;
import ai.chat2db.server.web.api.controller.rdb.vo.MetaSchemaVO;
import ai.chat2db.server.web.api.controller.rdb.vo.SchemaVO;
import ai.chat2db.server.web.api.controller.rdb.vo.SqlVO;
import ai.chat2db.server.web.api.controller.rdb.vo.TableVO;
import ai.chat2db.spi.model.Database;
import ai.chat2db.spi.model.ExecuteResult;
import ai.chat2db.spi.model.MetaSchema;
import ai.chat2db.spi.model.Schema;
import ai.chat2db.spi.model.Sql;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.model.TableIndex;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

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
    public abstract ColumnVO columnDto2vo(TableColumn dto);

    /**
     * 模型转换
     *
     * @param dtos
     * @return
     */
    public abstract List<ColumnVO> columnDto2vo(List<TableColumn> dtos);

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
}
