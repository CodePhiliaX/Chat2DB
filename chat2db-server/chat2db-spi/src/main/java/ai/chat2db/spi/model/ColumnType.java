package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ColumnType  implements Serializable {
    private static final long serialVersionUID = 1L;
    private String typeName;
    private boolean supportLength;
    private boolean supportScale;
    private boolean supportNullable;
    private boolean supportAutoIncrement;
    private boolean supportCharset;
    private boolean supportCollation;
    private boolean supportComments;
    private boolean supportDefaultValue;
    private boolean supportExtent;
    private boolean supportValue;
    private boolean supportUnit;

}
