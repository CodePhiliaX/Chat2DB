package ai.chat2db.spi.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class QueryResult implements Serializable {

    private String tableName;
    private List<Header> headerList;
    private List<ResultOperation> operations;
    private Map<String, Object> extra;
}
