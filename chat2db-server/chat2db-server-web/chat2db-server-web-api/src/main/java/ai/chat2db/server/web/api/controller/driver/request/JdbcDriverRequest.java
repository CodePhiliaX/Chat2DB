package ai.chat2db.server.web.api.controller.driver.request;

import java.util.List;

import lombok.Data;

@Data
public class JdbcDriverRequest {
    String jdbcDriverClass;
    String dbType;

    List<String> jdbcDriver;
}
