package ai.chat2db.server.start.test.druid;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class SqlUtilsTest2 {

    @Test
    public void coment() {
        SQLStatement sqlStatement = SQLUtils.parseSingleStatement(
            "comment on index myindex is '日期xxx';\n",
            DbType.h2);
        log.info("解析sql:{}", sqlStatement);
    }
}
