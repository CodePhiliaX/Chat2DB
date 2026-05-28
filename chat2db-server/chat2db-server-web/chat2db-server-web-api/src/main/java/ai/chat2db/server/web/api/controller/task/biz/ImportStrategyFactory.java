package ai.chat2db.server.web.api.controller.task.biz;

import ai.chat2db.server.tools.base.excption.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ImportStrategyFactory {

    @Autowired
    private List<ImportStrategy> strategies;

    public ImportStrategy getStrategy(String fileType) {
        for (ImportStrategy strategy : strategies) {
            if (strategy.supports(fileType)) {
                return strategy;
            }
        }
        throw new BusinessException("dataSource.unsupportedFileType", new Object[]{fileType});
    }
}
