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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditAdPage {
    private Stage stage;
    private ApiClient apiClient;
    private Ad existingAd;
    private Scene scene;
    private TextField titleField;
    private TextArea descriptionArea;
    private TextField priceField;
    private ComboBox<Category> categoryCombo;
    private ComboBox<City> cityCombo;
    private ListView<String> imagesListView;
    private List<String> imagePaths;
    private Label messageLabel;

    public EditAdPage(Stage stage, ApiClient apiClient, Ad existingAd) {
        this.stage = stage;
        this.apiClient = apiClient;
        this.existingAd = existingAd;
        this.imagePaths = new ArrayList<>(existingAd.getImagePaths());
        createScene();
    }

    private void createScene() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f0f4f8;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Button backButton = new Button("← بازگشت");
        backButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 8 15;");
        backButton.setOnAction(e -> new MyAdsPage(stage, apiClient).show());
        Label titleLabel = new Label("✏️ ویرایش آگهی");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        header.getChildren().addAll(backButton, titleLabel);

        VBox formBox = new VBox(15);
        formBox.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-border-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        int row = 0;

        form.add(new Label("عنوان:"), 0, row);
        titleField = new TextField(existingAd.getTitle());
        titleField.setPrefWidth(400);
        form.add(titleField, 1, row++);

        form.add(new Label("توضیحات:"), 0, row);
        descriptionArea = new TextArea(existingAd.getDescription());
        descriptionArea.setPrefWidth(400);
        descriptionArea.setPrefHeight(100);
        descriptionArea.setWrapText(true);
        form.add(descriptionArea, 1, row++);

        form.add(new Label("قیمت (تومان):"), 0, row);
        priceField = new TextField(String.valueOf(existingAd.getPrice()));
        priceField.setPrefWidth(200);
        form.add(priceField, 1, row++);

        form.add(new Label("دسته‌بندی:"), 0, row);
        categoryCombo = new ComboBox<>();
        categoryCombo.setPrefWidth(200);
        categoryCombo.setConverter(new javafx.util.StringConverter<Category>() {
            @Override public String toString(Category c) { return c == null ? "" : c.getName(); }
            @Override public Category fromString(String s) { return null; }
        });
        form.add(categoryCombo, 1, row++);

        form.add(new Label("شهر:"), 0, row);
        cityCombo = new ComboBox<>();
        cityCombo.setPrefWidth(200);
        cityCombo.setConverter(new javafx.util.StringConverter<City>() {
            @Override public String toString(City c) { return c == null ? "" : c.getName(); }
            @Override public City fromString(String s) { return null; }
        });
        form.add(cityCombo, 1, row++);

        form.add(new Label("تصاویر:"), 0, row);
        HBox imageBox = new HBox(10);
        imageBox.setAlignment(Pos.CENTER_LEFT);
        imagesListView = new ListView<>();
        imagesListView.setPrefWidth(250);
        imagesListView.setPrefHeight(80);
        for (String path : imagePaths) {
            imagesListView.getItems().add(new File(path).getName());
        }
        Button addImageButton = new Button("➕ افزودن تصویر");
        addImageButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 15;");
        Button removeImageButton = new Button("➖ حذف تصویر");
        removeImageButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5 15;");
        VBox imageButtons = new VBox(5);
        imageButtons.getChildren().addAll(addImageButton, removeImageButton);
        imageBox.getChildren().addAll(imagesListView, imageButtons);
        form.add(imageBox, 1, row++);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        Button submitButton = new Button("💾 ذخیره تغییرات");
        submitButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 15px; " +
                "-fx-padding: 10 40; -fx-border-radius: 5;");
        Button cancelButton = new Button("❌ انصراف");
        cancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10 30;");
        cancelButton.setOnAction(e -> new MyAdsPage(stage, apiClient).show());
        buttonBox.getChildren().addAll(submitButton, cancelButton);
        form.add(buttonBox, 1, row++);

        messageLabel = new Label();
        form.add(messageLabel, 1, row);

        formBox.getChildren().add(form);
        root.getChildren().addAll(header, formBox);

        addImageButton.setOnAction(e -> addImage());
        removeImageButton.setOnAction(e -> removeImage());
        submitButton.setOnAction(e -> submitEdit());

        this.scene = new Scene(root, 1000, 750);
        loadCategoriesAndCities();
    }

    private void loadCategoriesAndCities() {
        try {
            Response<List<Category>> catResponse = apiClient.getAllCategories();
            if (catResponse.isSuccess() && catResponse.getData() != null) {
                categoryCombo.getItems().setAll(catResponse.getData());
                categoryCombo.getItems().stream()
                        .filter(c -> c.getId().equals(existingAd.getCategoryId()))
                        .findFirst()
                        .ifPresent(categoryCombo::setValue);
            }
            Response<List<City>> cityResponse = apiClient.getAllCities();
            if (cityResponse.isSuccess() && cityResponse.getData() != null) {
                cityCombo.getItems().setAll(cityResponse.getData());
                cityCombo.getItems().stream()
                        .filter(c -> c.getId().equals(existingAd.getCityId()))
                        .findFirst()
                        .ifPresent(cityCombo::setValue);
            }
        } catch (Exception e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("خطا در دریافت اطلاعات: " + e.getMessage());
        }
    }

    private void addImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            imagePaths.add(file.getAbsolutePath());
            imagesListView.getItems().add(file.getName());
        }
    }

    private void removeImage() {
        int i = imagesListView.getSelectionModel().getSelectedIndex();
        if (i >= 0) {
            imagePaths.remove(i);
            imagesListView.getItems().remove(i);
        }
    }

    private void submitEdit() {
        if (categoryCombo.getValue() == null || cityCombo.getValue() == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("⚠️ لطفاً دسته‌بندی و شهر را انتخاب کنید");
            return;
        }
        try {
            AdRequest request = new AdRequest();
            request.setTitle(titleField.getText().trim());
            request.setDescription(descriptionArea.getText().trim());
            request.setPrice(Double.parseDouble(priceField.getText().trim()));
            request.setCategoryId(categoryCombo.getValue().getId());
            request.setCityId(cityCombo.getValue().getId());
            request.setImagePaths(new ArrayList<>(imagePaths));

            Long userId = apiClient.getCurrentUser().getId();
            Response<Ad> response = apiClient.updateAd(existingAd.getId(), userId, request);

            if (response.isSuccess()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("آگهی با موفقیت ویرایش شد.");
                alert.showAndWait();
                new MyAdsPage(stage, apiClient).show();
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("❌ " + response.getMessage());
            }
        } catch (NumberFormatException nfe) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("⚠️ قیمت را به‌درستی وارد کنید");
        } catch (Exception e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("❌ خطا: " + e.getMessage());
        }
    }

    public void show() {
        stage.setScene(scene);
        stage.setTitle("ویرایش آگهی");
    }
}