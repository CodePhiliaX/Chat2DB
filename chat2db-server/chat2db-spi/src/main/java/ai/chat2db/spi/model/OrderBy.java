package ai.chat2db.spi.model;

import lombok.Data;

@Data
public class OrderBy {

    /**
     * sort field
     */
    private String columnName;

    /**
     * sort by
     */
    private boolean asc;
}
