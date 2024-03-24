package ai.chat2db.server.web.api.controller.rdb.data;

import ai.chat2db.server.domain.api.param.datasource.DatabaseExportDataParam;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.SQLException;

/**
 * @author: zgq
 * @date: 2024年03月24日 12:46
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public abstract class ExportDBDataStrategy {

    public String suffix;
    public String contentType;

    public abstract void doExport(DatabaseExportDataParam param, HttpServletResponse response) throws SQLException;

}
