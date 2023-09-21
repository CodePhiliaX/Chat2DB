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
     * 序号
     **/
    private String no;
    /**
     * 字段名
     **/
    private String fieldName;
    /**
     * 数据类型
     **/
    private String columnType;
    /**
     * 长度
     **/
    private String length;
    /**
     * 不是null
     **/
    private String isNullAble;
    /**
     * 默认值
     **/
    private String columnDefault;
    /**
     * 小数位
     **/
    private String decimalPlaces;
    /**
     * 备注
     **/
    private String columnComment;

}
