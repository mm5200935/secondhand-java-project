package app.view.page;

import app.client.ApiClient;
import app.dto.response.Response;
import app.model.Ad;
import app.model.User;
import app.view.cell.AdListCell;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MyAdsPage {
    private Stage stage;
    private ApiClient apiClient;
    private Scene scene;
    private ListView<Ad> myAdsListView;
    private Label statusLabel;
    private ComboBox<String> filterCombo;
    private List<Ad> allMyAds = new ArrayList<>();


    public MyAdsPage(Stage stage, ApiClient apiClient) {
        this.stage = stage;
        this.apiClient = apiClient;
        createScene();
        loadMyAds();
    }

    private void createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f4f8;");

        // Top Bar
        root.setTop(createTopBar());

        // Center
        VBox centerContent = new VBox(15);
        centerContent.setPadding(new Insets(20));

        Label headerLabel = new Label("📋 آگهی‌های من");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        centerContent.getChildren().add(headerLabel);

        // Filter
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        Label filterLabel = new Label("فیلتر وضعیت:");
        filterLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll("همه", "فعال", "در انتظار بررسی", "فروخته شده", "رد شده");
        filterCombo.setValue("همه");
        filterCombo.setPrefWidth(150);
        filterCombo.setOnAction(e -> applyFilter());

        Button newAdButton = new Button("📝 ثبت آگهی جدید");
        newAdButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-padding: 8 15; " +
                "-fx-border-radius: 5;");
        newAdButton.setOnAction(e -> {
            NewAdPage newAdPage = new NewAdPage(stage, apiClient);
            newAdPage.show();
        });

        Button refreshButton = new Button("🔄 بروزرسانی");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15; " +
                "-fx-border-radius: 5;");
        refreshButton.setOnAction(e -> loadMyAds());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        filterBox.getChildren().addAll(filterLabel, filterCombo, spacer, newAdButton, refreshButton);
        centerContent.getChildren().add(filterBox);

        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
        centerContent.getChildren().add(statusLabel);

        myAdsListView = new ListView<>();
        myAdsListView.setCellFactory(param -> new AdListCell(apiClient, stage, this));
        myAdsListView.setStyle("-fx-background-color: transparent; -fx-border: none;");
        VBox.setVgrow(myAdsListView, Priority.ALWAYS);
        myAdsListView.setPrefHeight(500);

        centerContent.getChildren().add(myAdsListView);

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

        Label titleLabel = new Label("📋 آگهی‌های من");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        topBar.getChildren().addAll(backButton, titleLabel);
        return topBar;
    }



    private void loadMyAds() {
        try {
            User currentUser = apiClient.getCurrentUser();
            if (currentUser == null) {
                statusLabel.setText("⚠️ لطفاً ابتدا وارد شوید");
                return;
            }
            Response<List<Ad>> response = apiClient.getUserAds(currentUser.getId());
            if (response.isSuccess() && response.getData() != null) {
                allMyAds = response.getData();          // 👈 لیست کامل رو کش کن
                statusLabel.setText("📋 " + allMyAds.size() + " آگهی");
                applyFilter();                            // این دیگه امنه
            } else {
                statusLabel.setText("❌ " + response.getMessage());
            }
        } catch (Exception e) {
            statusLabel.setText("❌ خطا در دریافت آگهی‌ها: " + e.getMessage());
        }
    }

    private void applyFilter() {
        String filter = filterCombo.getValue();
        if (filter == null || filter.equals("همه")) {
            myAdsListView.getItems().setAll(allMyAds);   // 👈 دیگه loadMyAds صدا نمی‌زنه
            return;
        }

        Ad.AdStatus targetStatus;
        switch (filter) {
            case "فعال": targetStatus = Ad.AdStatus.ACTIVE; break;
            case "در انتظار بررسی": targetStatus = Ad.AdStatus.PENDING; break;
            case "فروخته شده": targetStatus = Ad.AdStatus.SOLD; break;
            case "رد شده": targetStatus = Ad.AdStatus.REJECTED; break;
            default: return;
        }

        List<Ad> filtered = allMyAds.stream()                // 👈 از کش فیلتر کن، نه از خودِ ListView
                .filter(ad -> ad.getStatus() == targetStatus)
                .collect(java.util.stream.Collectors.toList());

        myAdsListView.getItems().setAll(filtered);
        statusLabel.setText("📋 " + filtered.size() + " آگهی (" + filter + ")");
    }

    public void show() {
        stage.setScene(scene);
        stage.setTitle("آگهی‌های من");
    }
}