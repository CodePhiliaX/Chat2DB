package ai.chat2db.spi.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class TableMeta  implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<ColumnType> columnTypes;


    private List<Charset> charsets;


    private List<Collation> collations;


    private List<IndexType> indexTypes;

    private List<DefaultValue> defaultValues;

    private List<EngineType> engineTypes;
}
