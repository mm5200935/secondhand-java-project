package app.view.page;

import app.client.ApiClient;
import app.dto.request.AdRequest;
import app.dto.response.Response;
import app.model.Ad;
import app.model.Category;
import app.model.City;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NewAdPage {
    private Stage stage;
    private ApiClient apiClient;
    private Scene scene;
    private TextField titleField;
    private TextArea descriptionArea;
    private TextField priceField;
    private ComboBox<Category> categoryCombo;
    private ComboBox<City> cityCombo;
    private ListView<String> imagesListView;
    private List<String> imagePaths;
    private Label messageLabel;

    public NewAdPage(Stage stage, ApiClient apiClient) {
        this.stage = stage;
        this.apiClient = apiClient;
        this.imagePaths = new ArrayList<>();
        createScene();
    }

    private void createScene() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f0f4f8;");

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("← بازگشت");
        backButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 8 15;");
        backButton.setOnAction(e -> {
            HomePage homePage = new HomePage(stage, apiClient);
            homePage.show();
        });

        Label titleLabel = new Label("📝 ثبت آگهی جدید");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        header.getChildren().addAll(backButton, titleLabel);

        // Form
        VBox formBox = new VBox(15);
        formBox.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-border-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);

        int row = 0;

        // Title
        form.add(new Label("عنوان:"), 0, row);
        titleField = new TextField();
        titleField.setPromptText("عنوان آگهی را وارد کنید");
        titleField.setPrefWidth(400);
        form.add(titleField, 1, row++);

        // Description
        form.add(new Label("توضیحات:"), 0, row);
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("توضیحات کامل آگهی را وارد کنید");
        descriptionArea.setPrefWidth(400);
        descriptionArea.setPrefHeight(100);
        descriptionArea.setWrapText(true);
        form.add(descriptionArea, 1, row++);

        // Price
        form.add(new Label("قیمت (تومان):"), 0, row);
        priceField = new TextField();
        priceField.setPromptText("قیمت را وارد کنید");
        priceField.setPrefWidth(200);
        form.add(priceField, 1, row++);

        // Category
        form.add(new Label("دسته‌بندی:"), 0, row);
        categoryCombo = new ComboBox<>();
        categoryCombo.setPromptText("انتخاب دسته‌بندی");
        categoryCombo.setPrefWidth(200);

        categoryCombo.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                return category == null ? "" : category.getName();
            }
            @Override
            public Category fromString(String string) {
                return null;
            }
        });
        form.add(categoryCombo, 1, row++);

        // City
        form.add(new Label("شهر:"), 0, row);
        cityCombo = new ComboBox<>();
        cityCombo.setPromptText("انتخاب شهر");
        cityCombo.setPrefWidth(200);

        cityCombo.setConverter(new StringConverter<City>() {
            @Override
            public String toString(City city) {
                return city == null ? "" : city.getName();
            }
            @Override
            public City fromString(String string) {
                return null;
            }
        });
        form.add(cityCombo, 1, row++);

        // Images
        form.add(new Label("تصاویر:"), 0, row);
        HBox imageBox = new HBox(10);
        imageBox.setAlignment(Pos.CENTER_LEFT);

        imagesListView = new ListView<>();
        imagesListView.setPrefWidth(250);
        imagesListView.setPrefHeight(80);

        Button addImageButton = new Button("➕ افزودن تصویر");
        addImageButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 15;");

        Button removeImageButton = new Button("➖ حذف تصویر");
        removeImageButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5 15;");

        VBox imageButtons = new VBox(5);
        imageButtons.getChildren().addAll(addImageButton, removeImageButton);
        imageBox.getChildren().addAll(imagesListView, imageButtons);
        form.add(imageBox, 1, row++);

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button submitButton = new Button("📤 ثبت آگهی");
        submitButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 15px; " +
                "-fx-padding: 10 40; -fx-border-radius: 5;");

        Button cancelButton = new Button("❌ انصراف");
        cancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10 30;");
        cancelButton.setOnAction(e -> {
            HomePage homePage = new HomePage(stage, apiClient);
            homePage.show();
        });

        buttonBox.getChildren().addAll(submitButton, cancelButton);
        form.add(buttonBox, 1, row++);

        // Message
        messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 13px;");
        form.add(messageLabel, 1, row);

        formBox.getChildren().add(form);
        root.getChildren().addAll(header, formBox);

        // ===== Events =====
        addImageButton.setOnAction(e -> addImage());
        removeImageButton.setOnAction(e -> removeImage());

        submitButton.setOnAction(e -> submitAd());

        this.scene = new Scene(root, 1000, 750);

        // Load categories and cities
        loadCategoriesAndCities();
    }

    private void loadCategoriesAndCities() {
        try {
            Response<List<Category>> catResponse = apiClient.getAllCategories();
            if (catResponse.isSuccess() && catResponse.getData() != null) {
                categoryCombo.getItems().setAll(catResponse.getData());
            }

            Response<List<City>> cityResponse = apiClient.getAllCities();
            if (cityResponse.isSuccess() && cityResponse.getData() != null) {
                cityCombo.getItems().setAll(cityResponse.getData());
            }
        } catch (Exception e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("خطا در دریافت اطلاعات: " + e.getMessage());
        }
    }

    private void addImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("انتخاب تصویر");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            String path = file.getAbsolutePath();
            imagePaths.add(path);
            imagesListView.getItems().add(file.getName());
        }
    }

    private void removeImage() {
        int selectedIndex = imagesListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            imagePaths.remove(selectedIndex);
            imagesListView.getItems().remove(selectedIndex);
        }
    }

    private void submitAd() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String priceText = priceField.getText().trim();

        // Validations
        if (title.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("⚠️ لطفاً عنوان آگهی را وارد کنید");
            return;
        }

        if (description.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("⚠️ لطفاً توضیحات آگهی را وارد کنید");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("⚠️ لطفاً قیمت معتبر وارد کنید");
            return;
        }

        if (categoryCombo.getValue() == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("⚠️ لطفاً دسته‌بندی را انتخاب کنید");
            return;
        }

        if (cityCombo.getValue() == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("⚠️ لطفاً شهر را انتخاب کنید");
            return;
        }

        try {
            AdRequest request = new AdRequest();
            request.setTitle(title);
            request.setDescription(description);
            request.setPrice(price);
            request.setCategoryId(categoryCombo.getValue().getId());
            request.setCityId(cityCombo.getValue().getId());
            request.setImagePaths(new ArrayList<>(imagePaths));

            Long ownerId = apiClient.getCurrentUser().getId();
            Response<Ad> response = apiClient.createAd(ownerId, request);

            if (response.isSuccess()) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("✅ " + response.getMessage());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("ثبت آگهی");
                alert.setHeaderText(null);
                alert.setContentText("آگهی شما با موفقیت ثبت شد و در انتظار تایید مدیر است.");
                alert.showAndWait();

                HomePage homePage = new HomePage(stage, apiClient);
                homePage.show();
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("❌ " + response.getMessage());
            }
        } catch (Exception e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("❌ خطا: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void show() {
        stage.setScene(scene);
        stage.setTitle("ثبت آگهی جدید");
    }
}