package ai.chat2db.server.web.api.controller.rdb;

import ai.chat2db.server.domain.api.param.DataGenerationRequest;
import ai.chat2db.server.domain.api.service.DataGenerationService;
import ai.chat2db.server.domain.api.service.DataGenerationRuleService;
import ai.chat2db.server.domain.api.vo.DataGenerationPreviewVO;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.rdb.converter.DataGenerationConverter;
import ai.chat2db.server.web.api.controller.rdb.request.DataGenerationRequestVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@ConnectionInfoAspect
@RequestMapping("/api/rdb/table/generate-data")
public class DataGenerationController {

    @Autowired
    private DataGenerationService dataGenerationService;

    @Autowired
    private DataGenerationRuleService ruleService;

    @PostMapping("/config")
    public ListResult<ai.chat2db.server.domain.api.param.ColumnConfigParam> getTableColumns(
            @RequestBody DataGenerationRequestVO requestVO) {
        try {
            DataGenerationRequest request = DataGenerationConverter.voToRequest(requestVO);
            return ListResult.of(dataGenerationService.getTableColumns(request));
        } catch (Exception e) {
            log.error("Failed to get table columns for data generation", e);
            return ListResult.error("获取表列信息失败: " + e.getMessage(), null);
        }
    }

    @PostMapping("/preview")
    public DataResult<DataGenerationPreviewVO> generatePreview(
            @RequestBody DataGenerationRequestVO requestVO) {
        try {
            DataGenerationRequest request = DataGenerationConverter.voToRequest(requestVO);
            request.setPreviewMode(true);
            request.setRowCount(10);
            return DataResult.of(dataGenerationService.generatePreview(request));
        } catch (Exception e) {
            log.error("Failed to generate data preview", e);
            return DataResult.error("GENERATE_PREVIEW_ERROR", "生成预览失败: " + e.getMessage());
        }
    }

    @PostMapping("/execute")
    public DataResult<Long> executeDataGeneration(
            @RequestBody DataGenerationRequestVO requestVO) {
        try {
            DataGenerationRequest request = DataGenerationConverter.voToRequest(requestVO);
            return DataResult.of(dataGenerationService.executeDataGeneration(request));
        } catch (Exception e) {
            log.error("Failed to execute data generation", e);
            return DataResult.error("EXECUTE_GENERATION_ERROR", "执行数据生成失败: " + e.getMessage());
        }
    }

    @GetMapping("/templates")
    public ListResult<ai.chat2db.server.domain.api.param.GeneratorTemplate> getAllGeneratorTemplates() {
        try {
            return ListResult.of(dataGenerationService.getAllGeneratorTemplates());
        } catch (Exception e) {
            log.error("Failed to get all generator templates", e);
            return ListResult.error("获取生成模板失败: " + e.getMessage(), null);
        }
    }

    @GetMapping("/generation-rule/list")
    public ListResult<ai.chat2db.server.domain.api.param.ColumnConfigParam> getColumnConfigs(
            @RequestParam Long dataSourceId,
            @RequestParam String databaseName,
            @RequestParam(required = false) String schemaName,
            @RequestParam String tableName) {
        try {
            return ListResult.of(ruleService.getColumnConfigs(dataSourceId, databaseName, schemaName, tableName));
        } catch (Exception e) {
            log.error("Failed to get column configs", e);
            return ListResult.error("GET_COLUMN_CONFIGS_ERROR", "获取列配置失败: " + e.getMessage());
        }
    }
}
