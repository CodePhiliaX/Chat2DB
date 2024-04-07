package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class EngineType  implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private boolean supportTTL;
    private boolean supportSortOrder;
    private boolean supportSkippingIndices;
    private boolean supportDeduplication;
    private boolean supportSettings;
    private boolean supportParallelInsert;
    private boolean supportProjections;
    private boolean supportReplication;

}
