package hex.loan.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnUtil {

    public static Connection getDBConn() {
        Connection conn = null;
        try {
            Properties props = DBPropertyUtil.getPropertyString("db/db.properties");
            String driver = props.getProperty("driver");
            String url = props.getProperty("url");
            String username = props.getProperty("username");
            String password = props.getProperty("password");

            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Database connection failed.");
            e.printStackTrace();
        }

        return conn;
    }
}
