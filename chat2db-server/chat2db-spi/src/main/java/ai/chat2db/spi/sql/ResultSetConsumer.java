package ai.chat2db.spi.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetConsumer {

    void accept(ResultSet resultSet) throws SQLException;
}
