package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MetaSchema {

    /**
     * database list
     */
    private List<Database> databases;

    /**
     * schema list
     */
    private List<Schema> schemas;
}
