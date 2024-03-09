package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Collation  implements Serializable {
    private static final long serialVersionUID = 1L;

    private String collationName;
}
