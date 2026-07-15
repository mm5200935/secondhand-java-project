package app.database;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    // Default admin account created automatically the very first time the
    // database is initialized, so there is always at least one admin in the
    // system without having to edit the database by hand.
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

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

            seedDefaultAdmin(connection);


            statement.execute("""
                    CREATE TABLE IF NOT EXISTS categories (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE,
                        description TEXT
                    );
            """);

            seedDefaultCategories(connection);


            statement.execute("""
                    CREATE TABLE IF NOT EXISTS cities (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE,
                        province TEXT
                    );
            """);

            seedDefaultCities(connection);


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
                        
                        rejection_reason TEXT,
                        
                        FOREIGN KEY (seller_id) REFERENCES users(id),
                        FOREIGN KEY (category_id) REFERENCES categories(id),
                        FOREIGN KEY (city_id) REFERENCES cities(id)
                    );
            """);

            System.out.println("Advertisements table created.");

            try {
                statement.execute("ALTER TABLE advertisements ADD COLUMN rejection_reason TEXT");
            } catch (SQLException ignored) {
                // ستون از قبل وجود داره
            }


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

    /**
     * Inserts a default ADMIN account (username "admin", password
     * "admin123") the first time the database is created. If a user with
     * that username already exists (e.g. on subsequent app restarts, or if
     * someone renamed/changed it), nothing happens.
     */
    private static void seedDefaultAdmin(Connection connection) throws Exception {

        String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (PreparedStatement check = connection.prepareStatement(checkSql)) {

            check.setString(1, DEFAULT_ADMIN_USERNAME);

            var resultSet = check.executeQuery();
            resultSet.next();

            if (resultSet.getInt(1) > 0) {
                return; // admin already exists, nothing to do
            }
        }

        String hashedPassword = new BCryptPasswordEncoder().encode(DEFAULT_ADMIN_PASSWORD);

        String insertSql = """
                INSERT INTO users (username, password, full_name, email, phone, role, status)
                VALUES (?, ?, ?, ?, ?, 'ADMIN', 'ACTIVE')
                """;

        try (PreparedStatement insert = connection.prepareStatement(insertSql)) {

            insert.setString(1, DEFAULT_ADMIN_USERNAME);
            insert.setString(2, hashedPassword);
            insert.setString(3, "System Admin");
            insert.setString(4, "admin@secondhand.local");
            insert.setString(5, "00000000000");

            insert.executeUpdate();
        }

        System.out.println("Default admin account created -> username: '"
                + DEFAULT_ADMIN_USERNAME + "', password: '" + DEFAULT_ADMIN_PASSWORD + "'");
    }

    private static void seedDefaultCities(Connection connection) throws Exception {

        String checkSql = "SELECT COUNT(*) FROM cities";

        try (PreparedStatement check = connection.prepareStatement(checkSql)) {
            var resultSet = check.executeQuery();
            resultSet.next();

            if (resultSet.getInt(1) > 0) {
                return;
            }
        }

        String[][] cities = {
                {"تبریز", "آذربایجان شرقی"},
                {"ارومیه", "آذربایجان غربی"},
                {"اردبیل", "اردبیل"},
                {"اصفهان", "اصفهان"},
                {"کرج", "البرز"},
                {"ایلام", "ایلام"},
                {"بوشهر", "بوشهر"},
                {"تهران", "تهران"},
                {"شهرکرد", "چهارمحال و بختیاری"},
                {"بیرجند", "خراسان جنوبی"},
                {"مشهد", "خراسان رضوی"},
                {"بجنورد", "خراسان شمالی"},
                {"اهواز", "خوزستان"},
                {"زنجان", "زنجان"},
                {"سمنان", "سمنان"},
                {"زاهدان", "سیستان و بلوچستان"},
                {"شیراز", "فارس"},
                {"قزوین", "قزوین"},
                {"قم", "قم"},
                {"سنندج", "کردستان"},
                {"کرمان", "کرمان"},
                {"کرمانشاه", "کرمانشاه"},
                {"یاسوج", "کهگیلویه و بویراحمد"},
                {"گرگان", "گلستان"},
                {"رشت", "گیلان"},
                {"خرم‌آباد", "لرستان"},
                {"ساری", "مازندران"},
                {"اراک", "مرکزی"},
                {"بندرعباس", "هرمزگان"},
                {"همدان", "همدان"},
                {"یزد", "یزد"}
        };

        String insertSql = "INSERT INTO cities (name, province) VALUES (?, ?)";

        try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
            for (String[] city : cities) {
                insert.setString(1, city[0]);
                insert.setString(2, city[1]);
                insert.addBatch();
            }
            insert.executeBatch();
        }

        System.out.println("31 provincial-capital cities seeded.");
    }


    private static void seedDefaultCategories(Connection connection) throws Exception {

        String checkSql = "SELECT COUNT(*) FROM categories";

        try (PreparedStatement check = connection.prepareStatement(checkSql)) {
            var resultSet = check.executeQuery();
            resultSet.next();

            if (resultSet.getInt(1) > 0) {
                return;
            }
        }

        String[][] categories = {
                {"موبایل و تبلت", "گوشی موبایل، تبلت و لوازم جانبی آن‌ها"},
                {"لپ‌تاپ و کامپیوتر", "لپ‌تاپ، کامپیوتر رومیزی و قطعات مرتبط"},
                {"لوازم صوتی و تصویری", "تلویزیون، هدفون، اسپیکر و تجهیزات صوتی تصویری"},
                {"لوازم خانگی", "یخچال، ماشین لباسشویی و سایر لوازم خانگی"},
                {"مبلمان و دکوراسیون", "مبل، میز، صندلی و اقلام دکوراسیون منزل"},
                {"پوشاک و اکسسوری", "لباس، کیف، کفش و اکسسوری"},
                {"کتاب، لوازم‌التحریر و مجله", "کتاب، مجله و لوازم‌التحریر"},
                {"ورزش و سرگرمی", "لوازم ورزشی و وسایل سرگرمی"},
                {"خودرو", "خرید و فروش انواع خودرو"},
                {"موتورسیکلت", "خرید و فروش موتورسیکلت"},
                {"لوازم و قطعات خودرو", "قطعات یدکی و لوازم جانبی خودرو"},
                {"املاک", "خرید، فروش و اجاره ملک"},
                {"کودک و نوزاد", "لوازم و اسباب‌بازی کودک و نوزاد"},
                {"آرایشی و بهداشتی", "محصولات آرایشی و بهداشتی"},
                {"ابزار و تجهیزات صنعتی", "ابزارآلات و تجهیزات صنعتی"},
                {"حیوانات خانگی", "حیوانات خانگی و لوازم مربوطه"},
                {"آنتیک و کلکسیونی", "اقلام قدیمی، آنتیک و کلکسیونی"},
                {"سایر", "سایر کالاهایی که در دسته‌های بالا جای نمی‌گیرند"}
        };

        String insertSql = "INSERT INTO categories (name, description) VALUES (?, ?)";

        try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
            for (String[] category : categories) {
                insert.setString(1, category[0]);
                insert.setString(2, category[1]);
                insert.addBatch();
            }
            insert.executeBatch();
        }

        System.out.println("18 categories seeded.");
    }

}