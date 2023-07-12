package ai.chat2db.spi.model;

import java.io.Serializable;
import java.util.List;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MetaSchema implements Serializable {

    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;
    /**
     * database list
     */
    private List<Database> databases;

    /**
     * schema list
     */
    private List<Schema> schemas;
}
