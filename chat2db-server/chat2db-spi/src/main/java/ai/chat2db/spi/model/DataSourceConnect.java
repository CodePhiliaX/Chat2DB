package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 数据库连接对象
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceConnect {

    /**
     * 是否成功标志位
     */
    private Boolean success;

    /**
     * 失败消息提示
     * 只有失败的情况下会有
     */
    private String message;

    /**
     * 描述
     */
    private String description;

    /**
     * error detail
     */
    private String errorDetail;
}
