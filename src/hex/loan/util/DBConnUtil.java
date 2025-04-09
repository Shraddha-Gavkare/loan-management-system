package hex.loan.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnUtil {

    private static final String fileName = "db/db.properties"; // relative path from project root

    public static Connection getDbConnection() {
        Connection conn = null;
        String connStr = null;

        try {
            connStr = DBPropertyUtil.getConnectionString(fileName);
        } catch (IOException e) {
            System.out.println("Connection String Creation Failed");
            e.printStackTrace();
        }

        if (connStr != null) {
            try {
                conn = DriverManager.getConnection(connStr);
            } catch (SQLException e) {
                System.out.println("Error While Establishing DB Connection...");
                e.printStackTrace();
            }
        }
        return conn;
    }
}
