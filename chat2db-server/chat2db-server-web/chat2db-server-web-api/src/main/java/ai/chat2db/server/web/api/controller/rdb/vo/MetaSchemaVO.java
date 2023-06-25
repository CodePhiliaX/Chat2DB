package ai.chat2db.server.web.api.controller.rdb.vo;

import ai.chat2db.spi.model.Database;
import ai.chat2db.spi.model.Schema;
import lombok.Data;

import java.util.List;
@Data
public class MetaSchemaVO {
    /**
     * database list
     */
    private List<Database> databases;

    /**
     * schema list
     */
    private List<Schema> schemas;
}
