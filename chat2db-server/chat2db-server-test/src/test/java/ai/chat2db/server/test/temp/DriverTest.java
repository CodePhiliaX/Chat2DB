package ai.chat2db.server.test.temp;

import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;

public class DriverTest {

    public static void main(String[] args) throws SQLException {

        Driver driver = new org.mariadb.jdbc.Driver();
        DriverPropertyInfo[]  driverPropertyInfos = driver.getPropertyInfo("jdbc:mariadb://localhost:3306", null);
        System.out.println(driverPropertyInfos.length);
    }
}
