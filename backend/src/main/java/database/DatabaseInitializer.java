package database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {

        try (
                Connection connection = DatabaseManager.getConnection();
                Statement statement = connection.createStatement()
        ) {


            statement.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT NOT NULL UNIQUE,
                        password TEXT NOT NULL,
                        full_name TEXT,
                        email TEXT,
                        phone TEXT,
                        role TEXT NOT NULL,
                        status TEXT NOT NULL
                    );
            """);

            System.out.println("Users table created.");


            statement.execute("""
                    CREATE TABLE IF NOT EXISTS categories (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE,
                        description TEXT
                    );
            """);


            statement.execute("""
                    CREATE TABLE IF NOT EXISTS cities (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE,
                        province TEXT
                    );
            """);


            statement.execute("""
                    CREATE TABLE IF NOT EXISTS advertisements (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,

                        title TEXT NOT NULL,
                        description TEXT,

                        price REAL NOT NULL,
                        negotiable INTEGER NOT NULL,

                        status TEXT NOT NULL,

                        created_at TEXT NOT NULL,
                        updated_at TEXT NOT NULL,

                        seller_id INTEGER NOT NULL,
                        category_id INTEGER NOT NULL,
                        city_id INTEGER NOT NULL,

                        FOREIGN KEY (seller_id) REFERENCES users(id),
                        FOREIGN KEY (category_id) REFERENCES categories(id),
                        FOREIGN KEY (city_id) REFERENCES cities(id)
                    );
            """);

            System.out.println("Advertisements table created.");


            statement.execute("""
                    CREATE TABLE IF NOT EXISTS advertisement_images (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        image_path TEXT NOT NULL,
                        advertisement_id INTEGER NOT NULL,
                        created_at TEXT,
                        updated_at TEXT,

                        FOREIGN KEY (advertisement_id)
                            REFERENCES advertisements(id)
                    );
            """);


            statement.execute("""
                    CREATE TABLE IF NOT EXISTS conversations (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,

                        buyer_id INTEGER NOT NULL,
                        seller_id INTEGER NOT NULL,
                        advertisement_id INTEGER NOT NULL,

                        closed INTEGER NOT NULL DEFAULT 0,

                        FOREIGN KEY (buyer_id) REFERENCES users(id),
                        FOREIGN KEY (seller_id) REFERENCES users(id),
                        FOREIGN KEY (advertisement_id) REFERENCES advertisements(id)
                    );
            """);


            statement.execute("""
                    CREATE TABLE IF NOT EXISTS messages (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,

                        conversation_id INTEGER NOT NULL,
                        sender_id INTEGER NOT NULL,

                        content TEXT NOT NULL,

                        sent_at TEXT,
                        seen INTEGER NOT NULL DEFAULT 0,

                        FOREIGN KEY (conversation_id)
                            REFERENCES conversations(id),

                        FOREIGN KEY (sender_id)
                            REFERENCES users(id)
                    );
            """);


            statement.execute("""
                    CREATE TABLE IF NOT EXISTS favorites (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,

                        user_id INTEGER NOT NULL,
                        advertisement_id INTEGER NOT NULL,

                        UNIQUE(user_id, advertisement_id),

                        FOREIGN KEY (user_id) REFERENCES users(id),
                        FOREIGN KEY (advertisement_id) REFERENCES advertisements(id)
                    );
            """);


            statement.execute("""
                    CREATE TABLE IF NOT EXISTS ratings (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,

                        buyer_id INTEGER NOT NULL,
                        seller_id INTEGER NOT NULL,
                        advertisement_id INTEGER NOT NULL,

                        score INTEGER NOT NULL,
                        comment TEXT,

                        FOREIGN KEY (buyer_id) REFERENCES users(id),
                        FOREIGN KEY (seller_id) REFERENCES users(id),
                        FOREIGN KEY (advertisement_id) REFERENCES advertisements(id)
                    );
            """);

            System.out.println("All tables created successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}