
package ai.chat2db.spi.model;

import java.io.Serializable;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : TableSchema.java
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Schema implements Serializable {
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;

    /**
     * databaseName
     */
    @JsonAlias({"TABLE_CATALOG","table_catalog"})
    private String databaseName;
    /**
     * 数据名字
     */
    @JsonAlias({"TABLE_SCHEM","table_schem"})
    private String name;


    private String comment;


    private String owner;
}