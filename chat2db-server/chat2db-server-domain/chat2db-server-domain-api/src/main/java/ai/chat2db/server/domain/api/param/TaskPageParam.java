package ai.chat2db.server.domain.api.param;

import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TaskPageParam  extends PageQueryParam implements Serializable {

    private Long dataSourceId;

    private String databaseName;

    private String schemaName;

    private String tableName;

    private String deleted;

    private Long userId;

    private List<String> taskType;

    private String taskStatus;

}
