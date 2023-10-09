package ai.chat2db.server.domain.api.param;

import lombok.Data;

import java.util.List;

@Data
public class SelectResultOperation {

    private String type;

    private List<String> dataList;

    private List<String> oldDataList;
}
