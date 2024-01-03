package ai.chat2db.spi.model;

import lombok.Data;

@Data
public class OrderBy {

    /**
     * 排序字段
     */
    private String columnName;

    /**
     * 排序方式
     */
    private boolean asc;
}
