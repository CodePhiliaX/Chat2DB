package ai.chat2db.server.tools.common.model.rdb.data.option.table;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author: zgq
 * @date: 2024年04月26日 9:32
 */
@Data
@EqualsAndHashCode(callSuper =true)
public class ImportNewTableOptions extends ImportTableOptions{

    /*
    * create table sql
    * */
    private List<String> sql;
}
