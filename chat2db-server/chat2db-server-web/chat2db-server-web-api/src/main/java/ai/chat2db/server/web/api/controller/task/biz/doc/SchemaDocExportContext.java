package ai.chat2db.server.web.api.controller.task.biz.doc;

import ai.chat2db.server.domain.api.model.TableParameter;
import ai.chat2db.server.domain.api.model.IndexInfo;
import ai.chat2db.server.web.api.controller.rdb.doc.conf.ExportOptions;
import ai.chat2db.server.web.api.controller.rdb.vo.TableVO;
import ai.chat2db.spi.model.Table;
import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class SchemaDocExportContext {

    private List<Table> tables;

    private String databaseName;

    private File file;

    private ExportOptions exportOptions;

    private Map<String, List<TableParameter>> tableParameterMap;

    private Map<String, List<IndexInfo>> indexMap;
}
