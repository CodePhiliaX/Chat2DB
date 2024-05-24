package ai.chat2db.spi;


import java.sql.ResultSet;
import java.sql.SQLException;

public interface SQLValueProcessor {

    String getSqlValueString(ResultSet rs, int index) throws SQLException;
}
