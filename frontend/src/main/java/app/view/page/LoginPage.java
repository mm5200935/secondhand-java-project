package app.view.page;

import app.client.ApiClient;
import app.dto.request.AuthRequest;
import app.dto.response.LoginResponse;
import app.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginPage {
    private Stage stage;
    private ApiClient apiClient;
    private Scene scene;

    public LoginPage(Stage stage, ApiClient apiClient) {
        this.stage = stage;
        this.apiClient = apiClient;
        createScene();
    }

    private void createScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #e8f0fe, #f0f4f8);");

        Label titleLabel = new Label("🏪 سامانه خرید و فروش دست دوم");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitleLabel = new Label("خوش آمدید");
        subtitleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");

        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-border-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        formBox.setMaxWidth(450);

        Label formTitle = new Label("ورود به حساب کاربری");
        formTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);

        Label userLabel = new Label("👤 نام کاربری");
        userLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        TextField usernameField = new TextField();
        usernameField.setPrefWidth(250);
        usernameField.setPromptText("نام کاربری خود را وارد کنید");
        usernameField.setStyle("-fx-padding: 8; -fx-border-radius: 5;");

        Label passLabel = new Label("🔒 رمز عبور");
        passLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(250);
        passwordField.setPromptText("رمز عبور خود را وارد کنید");
        passwordField.setStyle("-fx-padding: 8; -fx-border-radius: 5;");

        Button loginButton = new Button("ورود");
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 10 40; -fx-border-radius: 5;");
        loginButton.setDefaultButton(true);

        Button registerButton = new Button("ثبت نام");
        registerButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 10 40; -fx-border-radius: 5;");

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

        form.add(userLabel, 0, 0);
        form.add(usernameField, 1, 0);
        form.add(passLabel, 0, 1);
        form.add(passwordField, 1, 1);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(loginButton, registerButton);
        form.add(buttonBox, 1, 2);
        form.add(messageLabel, 1, 3);

        formBox.getChildren().addAll(formTitle, form);
        root.getChildren().addAll(titleLabel, subtitleLabel, formBox);

        // ===== این بخش اصلاح شده است =====
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("⚠️ لطفاً تمام فیلدها را پر کنید");
                return;
            }

            try {
                AuthRequest request = new AuthRequest(username, password);
                LoginResponse response = apiClient.login(request);

                if (response.isSuccess()) {
                    messageLabel.setStyle("-fx-text-fill: green;");
                    messageLabel.setText("✅ " + response.getMessage());

                    User user = response.getUser();
                    if (user.isAdmin()) {
                        AdminPage adminPage = new AdminPage(stage, apiClient);
                        adminPage.show();
                    } else {
                        HomePage homePage = new HomePage(stage, apiClient);
                        homePage.show();
                    }
                } else {
                    messageLabel.setStyle("-fx-text-fill: red;");
                    messageLabel.setText("❌ " + response.getMessage());
                }
            } catch (Exception ex) {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("❌ خطا در ارتباط با سرور: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        registerButton.setOnAction(e -> {
            RegisterPage registerPage = new RegisterPage(stage, apiClient);
            registerPage.show();
        });

        passwordField.setOnAction(e -> loginButton.fire());
        usernameField.setOnAction(e -> passwordField.requestFocus());

        this.scene = new Scene(root, 1000, 750);
    }

    public void show() {
        stage.setScene(scene);
        stage.setTitle("ورود به سامانه");
    }
}