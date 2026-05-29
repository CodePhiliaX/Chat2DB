package ai.chat2db.server.web.api.controller.ai.statemachine.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import ai.chat2db.spi.CommandExecutor;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.model.ExecuteResult;
import ai.chat2db.spi.model.Header;
import ai.chat2db.spi.sql.Chat2DBContext;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SqlExplainHelper {

    public ExplainResult executeExplain(String sql) {
        try {
            SqlBuilder sqlBuilder = Chat2DBContext.getSqlBuilder();
            String explainSql = sqlBuilder.buildExplainSql(sql);
            
            if (explainSql == null) {
                return ExplainResult.builder()
                    .success(false)
                    .errorMessage("EXPLAIN not supported for this database type")
                    .explainSql(sql)
                    .build();
            }

            MetaData metaData = Chat2DBContext.getMetaData();
            CommandExecutor executor = metaData.getCommandExecutor();
            java.sql.Connection connection = Chat2DBContext.getConnection();
            
            ExecuteResult result = executor.execute(explainSql, connection, false, null, null, metaData.getValueHandler());
            
            return ExplainResult.builder()
                .success(result.getSuccess())
                .explainSql(explainSql)
                .planRows(parsePlanRows(result))
                .formattedPlan(formatExplainResult(result))
                .errorMessage(result.getMessage())
                .build();
        } catch (Exception e) {
            log.error("[SqlExplainHelper] Failed to execute EXPLAIN", e);
            return ExplainResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .explainSql(sql)
                .build();
        }
    }

    private List<List<String>> parsePlanRows(ExecuteResult result) {
        List<List<String>> rows = new ArrayList<>();
        
        if (CollectionUtils.isEmpty(result.getDataList())) {
            return rows;
        }

        // 添加表头
        if (CollectionUtils.isNotEmpty(result.getHeaderList())) {
            rows.add(result.getHeaderList().stream()
                .map(header -> header == null ? "null" : header.getName())
                .collect(Collectors.toList()));
        }

        // 添加数据行
        for (List<String> dataRow : result.getDataList()) {
            rows.add(new ArrayList<>(dataRow));
        }

        return rows;
    }

    private String formatExplainResult(ExecuteResult result) {
        StringBuilder sb = new StringBuilder();
        
        if (CollectionUtils.isNotEmpty(result.getHeaderList())) {
            sb.append(result.getHeaderList().stream()
                .map(Header::getName)
                .collect(Collectors.joining(" | "))).append("\n");
            sb.append("-".repeat(80)).append("\n");
        }
        
        if (CollectionUtils.isNotEmpty(result.getDataList())) {
            for (List<String> row : result.getDataList()) {
                sb.append(String.join(" | ", row)).append("\n");
            }
        }
        
        return sb.toString();
    }
}
