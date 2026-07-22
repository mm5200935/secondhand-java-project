package app.view.page;

import app.client.ApiClient;
import app.dto.response.Response;
import app.model.Ad;
import app.model.User;
import app.view.cell.FavoriteAdListCell;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class FavoritesPage {
    private Stage stage;
    private ApiClient apiClient;
    private Scene scene;
    private ListView<Ad> favoritesListView;
    private Label statusLabel;

    public FavoritesPage(Stage stage, ApiClient apiClient) {
        this.stage = stage;
        this.apiClient = apiClient;
        createScene();
        loadFavorites();
    }

    private void createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f4f8;");

        // Top Bar
        root.setTop(createTopBar());

        // Center
        VBox centerContent = new VBox(15);
        centerContent.setPadding(new Insets(20));

        Label headerLabel = new Label("⭐ علاقه‌مندی‌های من");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        centerContent.getChildren().add(headerLabel);

        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
        centerContent.getChildren().add(statusLabel);

        favoritesListView = new ListView<>();
        favoritesListView.setCellFactory(param -> new FavoriteAdListCell(apiClient, stage, this));
        favoritesListView.setStyle("-fx-background-color: transparent; -fx-border: none;");
        VBox.setVgrow(favoritesListView, Priority.ALWAYS);
        favoritesListView.setPrefHeight(500);

        centerContent.getChildren().add(favoritesListView);

        ScrollPane scrollPane = new ScrollPane(centerContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f0f4f8; -fx-border: none;");
        root.setCenter(scrollPane);

        this.scene = new Scene(root, 1000, 750);
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setStyle("-fx-background-color: #2c3e50;");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("← بازگشت");
        backButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 8 15; " +
                "-fx-border-radius: 5;");
        backButton.setOnAction(e -> {
            HomePage homePage = new HomePage(stage, apiClient);
            homePage.show();
        });

        Label titleLabel = new Label("⭐ علاقه‌مندی‌ها");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshButton = new Button("🔄 بروزرسانی");
        refreshButton.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: #2c3e50; -fx-padding: 8 15; " +
                "-fx-border-radius: 5;");
        refreshButton.setOnAction(e -> loadFavorites());

        Button removeAllButton = new Button("🗑️ حذف همه");
        removeAllButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8 15; " +
                "-fx-border-radius: 5;");
        removeAllButton.setOnAction(e -> removeAllFavorites());

        topBar.getChildren().addAll(backButton, titleLabel, spacer, refreshButton, removeAllButton);
        return topBar;
    }

    private void loadFavorites() {
        try {
            User currentUser = apiClient.getCurrentUser();
            if (currentUser == null) {
                statusLabel.setText("⚠️ لطفاً ابتدا وارد شوید");
                return;
            }

            Response<List<Ad>> response = apiClient.getFavorites(currentUser.getId());
            if (response.isSuccess() && response.getData() != null) {
                favoritesListView.getItems().setAll(response.getData());
                statusLabel.setText("⭐ " + response.getData().size() + " آگهی در علاقه‌مندی‌ها");
            } else {
                statusLabel.setText("❌ " + response.getMessage());
            }
        } catch (Exception e) {
            statusLabel.setText("❌ خطا در دریافت علاقه‌مندی‌ها: " + e.getMessage());
        }
    }

    private void removeAllFavorites() {
        if (favoritesListView.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("اطلاع");
            alert.setHeaderText(null);
            alert.setContentText("لیست علاقه‌مندی‌ها خالی است.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("تایید حذف همه");
        confirm.setHeaderText(null);
        confirm.setContentText("آیا مطمئن هستید که می‌خواهید همه آگهی‌ها را از علاقه‌مندی‌ها حذف کنید؟");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                User currentUser = apiClient.getCurrentUser();
                boolean hasError = false;

                for (Ad ad : favoritesListView.getItems()) {
                    Response<Boolean> response = apiClient.removeFavorite(ad.getId(), currentUser.getId());
                    if (!response.isSuccess()) {
                        hasError = true;
                    }
                }

                if (hasError) {
                    statusLabel.setStyle("-fx-text-fill: orange;");
                    statusLabel.setText("⚠️ برخی از آگهی‌ها حذف نشدند. لطفاً دوباره تلاش کنید.");
                } else {
                    statusLabel.setStyle("-fx-text-fill: green;");
                    statusLabel.setText("✅ همه آگهی‌ها از علاقه‌مندی‌ها حذف شدند.");
                }

                loadFavorites();
            } catch (Exception e) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("❌ خطا: " + e.getMessage());
            }
        }
    }

    public void refreshFavorites() {
        loadFavorites();
    }

    public void show() {
        stage.setScene(scene);
        stage.setTitle("علاقه‌مندی‌ها");
    }
}