package ai.chat2db.server.web.api.controller.rdb;

import ai.chat2db.server.domain.api.param.ErDiagramQueryParam;
import ai.chat2db.server.domain.api.service.ErDiagramService;
import ai.chat2db.server.domain.api.vo.InferVirtualFkResultVO;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.rdb.request.ErDiagramQueryRequest;
import ai.chat2db.spi.model.ErDiagram;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ER图控制器
 * 提供ER图数据查询接口，用于展示数据库表之间的关系
 */
@ConnectionInfoAspect
@RequestMapping("/api/rdb/er")
@RestController
@Slf4j
public class ErDiagramController {

    @Autowired
    private ErDiagramService erDiagramService;

    /**
     * 查询ER图数据
     * 返回指定数据库中表之间的外键关系，用于前端渲染ER图
     *
     * @param request 查询请求，包含数据源、数据库、schema、表名过滤、虚拟外键开关等参数
     * @return ER图数据，包含节点（表）和边（外键关系）
     */
    @GetMapping("/diagram")
    public DataResult<ErDiagram> diagram(@Valid ErDiagramQueryRequest request) {
        ErDiagramQueryParam param = ErDiagramQueryParam.builder()
                .dataSourceId(request.getDataSourceId())
                .databaseName(request.getDatabaseName())
                .schemaName(request.getSchemaName())
                .tableNameFilter(request.getTableNameFilter())
                .includeVirtualFk(request.getIncludeVirtualFk())
                .syncForeignKeys(request.getSyncForeignKeys())
                .onlyRelatedTables(request.getOnlyRelatedTables())
                .build();
        return DataResult.of(erDiagramService.queryErDiagram(param));
    }

    /**
     * 推断并添加虚拟外键
     * 根据命名规范（如 user_id -> users.id）自动推断可能的虚拟外键关系
     *
     * @param request 查询请求
     * @return 推断结果，包含新增和删除的虚拟外键列表
     */
    @PostMapping("/infer-virtual-fk")
    public DataResult<InferVirtualFkResultVO> inferVirtualForeignKey(@Valid @RequestBody ErDiagramQueryRequest request) {
        ErDiagramQueryParam param = ErDiagramQueryParam.builder()
                .dataSourceId(request.getDataSourceId())
                .databaseName(request.getDatabaseName())
                .schemaName(request.getSchemaName())
                .tableNameFilter(request.getTableNameFilter())
                .includeVirtualFk(true)
                .build();
        return DataResult.of(erDiagramService.inferVirtualForeignKeys(param));
    }
}