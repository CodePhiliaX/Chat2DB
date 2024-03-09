package ai.chat2db.spi.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ResultOperation  implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;

    private List<String> dataList;

    private List<String> oldDataList;
}
