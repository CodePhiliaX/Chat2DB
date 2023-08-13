package ai.chat2db.spi.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetFunction<R> {

    R apply(ResultSet t) throws SQLException;
}
