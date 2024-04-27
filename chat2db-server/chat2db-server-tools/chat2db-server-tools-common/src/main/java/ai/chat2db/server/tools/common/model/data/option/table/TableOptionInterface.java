package ai.chat2db.server.tools.common.model.data.option.table;

import java.util.List;

/**
 * @author: zgq
 * @date: 2024年04月25日 23:49
 */
public interface TableOptionInterface {
    String getTableName();

    List<String> getTableColumns();

    List<String> getFileColumns();
}
