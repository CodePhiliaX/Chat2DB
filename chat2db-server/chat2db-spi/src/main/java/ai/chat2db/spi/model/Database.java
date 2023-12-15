package ai.chat2db.spi.model;

import java.io.Serializable;
import java.util.List;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 数据库
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Database implements Serializable {
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;
    /**
     * 数据库名字
     */
    @JsonAlias({"TABLE_CAT"})
    private String name;

    /**
     * schema name
     */
    private List<Schema> schemas;


    private String comment;

    private String charset;

    private String collation;

    private String owner;
}
