package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.param.DataGenerationRequest;
import ai.chat2db.server.domain.api.param.ColumnConfigParam;
import ai.chat2db.server.domain.api.param.GeneratorTemplate;
import ai.chat2db.server.domain.api.vo.DataGenerationPreviewVO;

import java.util.List;

public interface DataGenerationService {

    List<ColumnConfigParam> getTableColumns(DataGenerationRequest request);

    DataGenerationPreviewVO generatePreview(DataGenerationRequest request);

    Long executeDataGeneration(DataGenerationRequest request);

    List<GeneratorTemplate> getAllGeneratorTemplates();
}
