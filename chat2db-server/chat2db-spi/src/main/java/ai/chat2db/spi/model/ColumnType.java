package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ColumnType {
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
