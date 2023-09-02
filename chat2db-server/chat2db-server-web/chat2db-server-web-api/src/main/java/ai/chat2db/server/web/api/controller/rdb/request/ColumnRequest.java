package ai.chat2db.server.web.api.controller.rdb.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 列
 *
 * @author 是仪
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnRequest {

    /**
     * 旧的列名，在修改列的时候需要这个参数
     * 不修改也可以传
     */
    private String oldName;
    /**
     * 名称
     */
    private String name;

    /**
     * 列的类型
     * 比如 varchar(100) ,double(10,6)
     */
    private String columnType;

    /**
     * 是否为空
     */
    private Integer nullable;

    /**
     * 是否主键
     */
    private Boolean primaryKey;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 是否自增
     */
    private Boolean autoIncrement;

    /**
     * 注释
     */
    private String comment;
}
