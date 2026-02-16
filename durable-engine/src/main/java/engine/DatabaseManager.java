package engine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:durable.db";

    static {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String createTable = """
                    CREATE TABLE IF NOT EXISTS steps (
                        workflow_id TEXT,
                        step_key TEXT,
                        status TEXT,
                        output TEXT,
                        PRIMARY KEY (workflow_id, step_key)
                    );
                    """;

            stmt.execute(createTable);

        } catch (SQLException e) {
            throw new RuntimeException("Error initializing database", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
