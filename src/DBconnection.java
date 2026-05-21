import java.sql.Connection;
import java.sql.DriverManager;

public class DBconnection {
    static final String URL = "jdbc:mysql://localhost:3306/finance_app";
    static final String USER = "root";
    static final String PASSWORD = "YOUR_PASSWORD";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
