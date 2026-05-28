package ai.chat2db.server.web.api.controller.sql;

import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.sql.biz.SqlExecuteBizService;
import ai.chat2db.server.web.api.controller.sql.request.SqlFormatRequest;
import ai.chat2db.server.web.api.controller.sql.request.SqlFileExecuteRequest;
import com.github.vertical_blank.sqlformatter.SqlFormatter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * SQL Controller
 */
@ConnectionInfoAspect
@RequestMapping("/api/sql")
@RestController
public class SqlController {

    @Autowired
    private SqlExecuteBizService sqlExecuteBizService;

    /**
     * SQL Format
     *
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

    @PostMapping("/execute_file")
    public DataResult<Long> executeFile(
            @RequestPart("file") MultipartFile file,
            @ModelAttribute SqlFileExecuteRequest request) {
        return sqlExecuteBizService.executeSqlFile(file, request);
    }

}
