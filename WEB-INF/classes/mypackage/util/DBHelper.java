package mypackage.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBHelper {
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
                connection = DriverManager.getConnection("jdbc:sqlserver://localhost;database=Leave;integratedSecurity=true;trustServerCertificate=true;");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }

    public static ResultSet executeQuery(String sql) throws SQLException {
        return getConnection().createStatement().executeQuery(sql);
    }

    public static boolean executeUpdate(String sql) throws SQLException {
        return getConnection().createStatement().execute(sql);
    }
}
