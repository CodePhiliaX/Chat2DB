package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Charset {

    private String charsetName;

    private String defaultCollationName;
}
