package app;

import app.database.DatabaseInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        DatabaseInitializer.initialize();
        SpringApplication.run(App.class, args);
    }
}