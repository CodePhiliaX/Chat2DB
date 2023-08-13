package ai.chat2db.server.web.api.controller.sql.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SqlFormatRequest {

    private String sql;

    private String dbType;
}
