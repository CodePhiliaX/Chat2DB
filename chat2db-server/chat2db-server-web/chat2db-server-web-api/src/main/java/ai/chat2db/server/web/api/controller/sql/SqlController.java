package ai.chat2db.server.web.api.controller.sql;

import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.sql.request.SqlFormatRequest;
import cn.hutool.db.sql.SqlFormatter;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SQL Controller
 */
@ConnectionInfoAspect
@RequestMapping("/api/sql")
@RestController
public class SqlController {

    /**
     * SQL Format
     * @param sqlFormatRequest
     * @return
     */
    @GetMapping("/format")
    public DataResult<String> list(@Valid SqlFormatRequest sqlFormatRequest) {

        String sql = sqlFormatRequest.getSql();
        try {
            sql = SqlFormatter.format(sql);
        } catch (Exception e) {
            // ignore
        }
        return DataResult.of(sql);
    }
}
