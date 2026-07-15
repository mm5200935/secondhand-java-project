package app.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String DB_PATH = "database/secondhand.db";
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    public static Connection getConnection() throws SQLException {
        // اگر پوشه database وجود نداشته باشد، SQLite با خطای
        // SQLITE_CANTOPEN مواجه می‌شود؛ برای اطمینان خودمان می‌سازیمش.
        File dbFile = new File(DB_PATH);
        File parentDir = dbFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        return DriverManager.getConnection(URL);
    }

}
