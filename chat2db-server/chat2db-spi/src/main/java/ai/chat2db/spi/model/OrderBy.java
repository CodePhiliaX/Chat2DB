package ai.chat2db.spi.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderBy  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * sort field
     */
    private String columnName;

    /**
     * sort by
     */
    private boolean asc;
}
