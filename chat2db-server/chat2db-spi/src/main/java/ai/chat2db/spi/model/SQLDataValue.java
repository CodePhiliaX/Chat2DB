package ai.chat2db.spi.model;

import lombok.Data;

/**
 * @author: zgq
 * @date: 2024年05月30日 15:01
 */
@Data
public class SQLDataValue {
    private String value;
    private DataType dataType;

    public String getDateTypeName(){
        return dataType.getDataTypeName();
    }

    public int getPrecision(){
        return dataType.getPrecision();
    }
}
