package ai.chat2db.server.domain.api.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * TableParameter
 *
 * @author lzy
 **/
@Data
@Accessors(chain = true)
public class TableParameter {
    /**
     * serial number
     **/
    private String no;
    /**
     * Field name
     **/
    private String fieldName;
    /**
     * type of data
     **/
    private String columnType;
    /**
     * length
     **/
    private String length;
    /**
     * not null
     **/
    private String isNullAble;
    /**
     * default value
     **/
    private String columnDefault;
    /**
     * Decimal places
     **/
    private String decimalPlaces;
    /**
     * Remark
     **/
    private String columnComment;

}
