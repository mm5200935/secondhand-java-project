package app.view.page;

import app.client.ApiClient;
import app.dto.request.RegisterRequest;
import app.dto.response.Response;
import app.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterPage {
    private Stage stage;
    private ApiClient apiClient;
    private Scene scene;

    public RegisterPage(Stage stage, ApiClient apiClient) {
        this.stage = stage;
        this.apiClient = apiClient;
        createScene();
    }

    private void createScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #e8f4f8, #f0f4f8);");

        Label titleLabel = new Label("📝 ثبت نام در سامانه");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(12);
        form.setAlignment(Pos.CENTER);
        form.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        // Fields
        Label fullNameLabel = new Label("نام کامل:");
        fullNameLabel.setStyle("-fx-font-weight: bold;");
        TextField fullNameField = new TextField();
        fullNameField.setPrefWidth(250);
        fullNameField.setPromptText("نام و نام خانوادگی");

        Label usernameLabel = new Label("نام کاربری:");
        usernameLabel.setStyle("-fx-font-weight: bold;");
        TextField usernameField = new TextField();
        usernameField.setPrefWidth(250);
        usernameField.setPromptText("نام کاربری خود را انتخاب کنید");

        Label passwordLabel = new Label("رمز عبور:");
        passwordLabel.setStyle("-fx-font-weight: bold;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefWidth(250);
        passwordField.setPromptText("رمز عبور خود را وارد کنید");

        Label confirmLabel = new Label("تکرار رمز:");
        confirmLabel.setStyle("-fx-font-weight: bold;");
        PasswordField confirmField = new PasswordField();
        confirmField.setPrefWidth(250);
        confirmField.setPromptText("رمز عبور را تکرار کنید");

        Label phoneLabel = new Label("شماره تماس:");
        phoneLabel.setStyle("-fx-font-weight: bold;");
        TextField phoneField = new TextField();
        phoneField.setPrefWidth(250);
        phoneField.setPromptText("مثال: 09123456789");

        Label emailLabel = new Label("ایمیل:");
        emailLabel.setStyle("-fx-font-weight: bold;");
        TextField emailField = new TextField();
        emailField.setPrefWidth(250);
        emailField.setPromptText("ایمیل خود را وارد کنید (اختیاری)");

        int row = 0;
        form.add(fullNameLabel, 0, row);
        form.add(fullNameField, 1, row++);
        form.add(usernameLabel, 0, row);
        form.add(usernameField, 1, row++);
        form.add(passwordLabel, 0, row);
        form.add(passwordField, 1, row++);
        form.add(confirmLabel, 0, row);
        form.add(confirmField, 1, row++);
        form.add(phoneLabel, 0, row);
        form.add(phoneField, 1, row++);
        form.add(emailLabel, 0, row);
        form.add(emailField, 1, row++);

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 12px;");

        Button registerButton = new Button("✅ ثبت نام");
        registerButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-border-radius: 5;");
        registerButton.setPrefWidth(150);

        Button backButton = new Button("🔙 بازگشت");
        backButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30; -fx-border-radius: 5;");
        backButton.setPrefWidth(150);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(registerButton, backButton);
        form.add(buttonBox, 1, row++);
        form.add(messageLabel, 1, row);

        // Event handlers
        registerButton.setOnAction(e -> {
            String fullName = fullNameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String confirm = confirmField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            // Validations
            if (fullName.isEmpty()) {
                messageLabel.setStyle("-fx-text-fill: #e74c3c;");
                messageLabel.setText("❌ نام کامل نمی‌تواند خالی باشد");
                return;
            }
            if (username.isEmpty()) {
                messageLabel.setStyle("-fx-text-fill: #e74c3c;");
                messageLabel.setText("❌ نام کاربری نمی‌تواند خالی باشد");
                return;
            }
            if (username.length() < 3) {
                messageLabel.setStyle("-fx-text-fill: #e74c3c;");
                messageLabel.setText("❌ نام کاربری حداقل 3 کاراکتر باشد");
                return;
            }
            if (password.isEmpty()) {
                messageLabel.setStyle("-fx-text-fill: #e74c3c;");
                messageLabel.setText("❌ رمز عبور نمی‌تواند خالی باشد");
                return;
            }
            if (password.length() < 6) {
                messageLabel.setStyle("-fx-text-fill: #e74c3c;");
                messageLabel.setText("❌ رمز عبور حداقل 6 کاراکتر باشد");
                return;
            }
            if (!password.equals(confirm)) {
                messageLabel.setStyle("-fx-text-fill: #e74c3c;");
                messageLabel.setText("❌ رمز عبور و تکرار آن یکسان نیست");
                return;
            }
            if (phone.isEmpty()) {
                messageLabel.setStyle("-fx-text-fill: #e74c3c;");
                messageLabel.setText("❌ شماره تماس نمی‌تواند خالی باشد");
                return;
            }
            if (!phone.matches("^09\\d{9}$")) {
                messageLabel.setStyle("-fx-text-fill: #e74c3c;");
                messageLabel.setText("❌ شماره تماس معتبر نیست (مثال: 09123456789)");
                return;
            }
            if (!email.isEmpty() && !email.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
                messageLabel.setStyle("-fx-text-fill: #e74c3c;");
                messageLabel.setText("❌ فرمت ایمیل معتبر نیست");
                return;
            }

            registerButton.setDisable(true);
            registerButton.setText("⏳ در حال ثبت نام...");

            try {
                RegisterRequest request = new RegisterRequest();
                request.setFullName(fullName);
                request.setUsername(username);
                request.setPassword(password);
                request.setPhoneNumber(phone);
                request.setEmail(email);

                Response<User> response = apiClient.register(request);

                if (response.isSuccess()) {
                    messageLabel.setStyle("-fx-text-fill: #27ae60;");
                    messageLabel.setText("✅ " + response.getMessage());

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("ثبت نام موفق");
                    alert.setHeaderText(null);
                    alert.setContentText("ثبت نام شما با موفقیت انجام شد.\nاکنون می‌توانید وارد سیستم شوید.");
                    alert.showAndWait();

                    // Go back to login
                    LoginPage loginPage = new LoginPage(stage, apiClient);
                    loginPage.show();
                } else {
                    messageLabel.setStyle("-fx-text-fill: #e74c3c;");
                    messageLabel.setText("❌ " + response.getMessage());
                    registerButton.setDisable(false);
                    registerButton.setText("✅ ثبت نام");
                }
            } catch (Exception ex) {
                messageLabel.setStyle("-fx-text-fill: #e74c3c;");
                messageLabel.setText("❌ خطا در ارتباط با سرور: " + ex.getMessage());
                registerButton.setDisable(false);
                registerButton.setText("✅ ثبت نام");
            }
        });

        backButton.setOnAction(e -> {
            LoginPage loginPage = new LoginPage(stage, apiClient);
            loginPage.show();
        });

        VBox centerBox = new VBox(15);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().addAll(titleLabel, form);
        root.getChildren().add(centerBox);

        this.scene = new Scene(root, 1000, 750);
    }

    public void show() {
        stage.setScene(scene);
        stage.setTitle("ثبت نام در سامانه");
    }
}