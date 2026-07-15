package app.view.page;

import app.client.ApiClient;
import app.dto.request.SearchRequest;
import app.dto.response.Response;
import app.model.Ad;
import app.model.Category;
import app.model.City;
import app.view.cell.AdListCell;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

public class HomePage {
    private Stage stage;
    private ApiClient apiClient;
    private Scene scene;
    private VBox contentArea;
    private ListView<Ad> adListView;
    private ComboBox<Category> categoryCombo;
    private ComboBox<City> cityCombo;
    private ComboBox<String> sortCombo;
    private TextField searchField;
    private TextField minPriceField;
    private TextField maxPriceField;
    private Label statusLabel;

    public HomePage(Stage stage, ApiClient apiClient) {
        this.stage = stage;
        this.apiClient = apiClient;
        createScene();
    }

    private void createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f4f8;");

        // Top Bar
        HBox topBar = createTopBar();
        root.setTop(topBar);

        // Content Area
        contentArea = new VBox(15);
        contentArea.setPadding(new Insets(20));

        // Search Section
        VBox searchSection = createSearchSection();
        contentArea.getChildren().add(searchSection);

        // Status Label
        statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        contentArea.getChildren().add(statusLabel);

        // Ad List
        adListView = new ListView<>();
        adListView.setCellFactory(param -> new AdListCell(apiClient, stage, this));
        adListView.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        adListView.setPrefHeight(500);
        contentArea.getChildren().add(adListView);

        // ScrollPane for content
        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f0f4f8; -fx-border-color: transparent;");
        root.setCenter(scrollPane);

        this.scene = new Scene(root, 1000, 750);

        // Load initial data
        loadAds();
        loadCategoriesAndCities();
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setStyle("-fx-background-color: #2c3e50;");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("🏠 صفحه اصلی");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String userName = apiClient.getCurrentUser() != null ?
                apiClient.getCurrentUser().getFullName() : "کاربر";
        Label userLabel = new Label("👤 " + userName);
        userLabel.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 14px;");

        Button newAdButton = createButton("📝 ثبت آگهی", "#e67e22");
        Button favoritesButton = createButton("⭐ علاقه‌مندی‌ها", "#f1c40f");
        Button messagesButton = createButton("💬 پیام‌ها", "#3498db");
        Button logoutButton = createButton("🚪 خروج", "#e74c3c");
        Button myAdsButton = createButton("📋 آگهی‌های من", "#9b59b6");

        newAdButton.setOnAction(e -> {
            NewAdPage newAdPage = new NewAdPage(stage, apiClient);
            newAdPage.show();
        });

        favoritesButton.setOnAction(e -> {
            FavoritesPage favoritesPage = new FavoritesPage(stage, apiClient);
            favoritesPage.show();
        });

        messagesButton.setOnAction(e -> {
            ConversationsPage conversationsPage = new ConversationsPage(stage, apiClient);
            conversationsPage.show();
        });

        logoutButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("خروج");
            alert.setHeaderText(null);
            alert.setContentText("آیا مطمئن هستید که می‌خواهید خارج شوید؟");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    apiClient.logout();
                    LoginPage loginPage = new LoginPage(stage, apiClient);
                    loginPage.show();
                }
            });
        });

        myAdsButton.setOnAction(e -> {
            MyAdsPage myAdsPage = new MyAdsPage(stage, apiClient);
            myAdsPage.show();
        });

        topBar.getChildren().addAll(titleLabel, spacer, userLabel, newAdButton, myAdsButton,
                favoritesButton, messagesButton, logoutButton);

        return topBar;
    }

    private Button createButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: " +
                (color.equals("#f1c40f") ? "#2c3e50" : "white") +
                "; -fx-padding: 8 15; -fx-border-radius: 5; -fx-font-weight: bold;");
        return button;
    }

    private VBox createSearchSection() {
        VBox searchBox = new VBox(10);
        searchBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);");

        HBox searchRow = new HBox(10);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        searchField = new TextField();
        searchField.setPromptText("🔍 جست‌وجو در آگهی‌ها...");
        searchField.setPrefWidth(250);
        searchField.setStyle("-fx-padding: 8; -fx-border-radius: 5;");

        categoryCombo = new ComboBox<>();
        categoryCombo.setPromptText("📂 دسته‌بندی");
        categoryCombo.setPrefWidth(150);

        cityCombo = new ComboBox<>();
        cityCombo.setPromptText("📍 شهر");
        cityCombo.setPrefWidth(150);

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

        minPriceField = new TextField();
        minPriceField.setPromptText("💰 قیمت از");
        minPriceField.setPrefWidth(100);
        minPriceField.setStyle("-fx-padding: 8; -fx-border-radius: 5;");

        maxPriceField = new TextField();
        maxPriceField.setPromptText("💰 قیمت تا");
        maxPriceField.setPrefWidth(100);
        maxPriceField.setStyle("-fx-padding: 8; -fx-border-radius: 5;");

        sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll("مرتب‌سازی پیش‌فرض", "ارزان‌ترین", "گران‌ترین", "جدیدترین");
        sortCombo.setValue("مرتب‌سازی پیش‌فرض");
        sortCombo.setPrefWidth(140);

        Button searchButton = new Button("🔍 جست‌وجو");
        searchButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 20; -fx-border-radius: 5; -fx-font-weight: bold;");

        Button resetButton = new Button("🔄 پاک کردن");
        resetButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 8 20; -fx-border-radius: 5;");


        searchRow.getChildren().addAll(searchField, categoryCombo, cityCombo,
                minPriceField, maxPriceField, sortCombo, searchButton, resetButton);

        searchBox.getChildren().add(searchRow);

        // Event handlers
        searchButton.setOnAction(e -> performSearch());
        resetButton.setOnAction(e -> resetSearch());
        searchField.setOnAction(e -> performSearch());

        return searchBox;
    }

    private void performSearch() {
        SearchRequest request = new SearchRequest();
        request.setKeyword(searchField.getText().trim());

        Category selectedCategory = categoryCombo.getValue();
        if (selectedCategory != null) {
            request.setCategoryId(selectedCategory.getId());
        }

        City selectedCity = cityCombo.getValue();
        if (selectedCity != null) {
            request.setCityId(selectedCity.getId());
        }

        try {
            if (!minPriceField.getText().trim().isEmpty()) {
                request.setMinPrice(Double.parseDouble(minPriceField.getText().trim()));
            }
            if (!maxPriceField.getText().trim().isEmpty()) {
                request.setMaxPrice(Double.parseDouble(maxPriceField.getText().trim()));
            }
        } catch (NumberFormatException ex) {
            showAlert("خطا", "لطفاً قیمت را به درستی وارد کنید");
            return;
        }

        try {
            statusLabel.setText("⏳ در حال جست‌وجو...");
            Response<List<Ad>> response = apiClient.searchAds(request);
            if (response.isSuccess() && response.getData() != null) {
                List<Ad> ads = applySort(response.getData());
                adListView.getItems().setAll(ads);
                statusLabel.setText("✅ " + response.getData().size() + " آگهی یافت شد");
            } else {
                statusLabel.setText("❌ " + response.getMessage());
            }
        } catch (Exception e) {
            statusLabel.setText("❌ خطا در ارتباط با سرور");
            showAlert("خطا", "خطا در ارتباط با سرور: " + e.getMessage());
        }
    }

    private void resetSearch() {
        searchField.clear();
        categoryCombo.setValue(null);
        cityCombo.setValue(null);
        minPriceField.clear();
        maxPriceField.clear();
        sortCombo.setValue("مرتب‌سازی پیش‌فرض");
        loadAds();
    }

    private List<Ad> applySort(List<Ad> ads) {
        List<Ad> sorted = new java.util.ArrayList<>(ads);
        String sortValue = sortCombo.getValue();
        if ("ارزان‌ترین".equals(sortValue)) {
            sorted.sort(java.util.Comparator.comparingDouble(Ad::getPrice));
        } else if ("گران‌ترین".equals(sortValue)) {
            sorted.sort(java.util.Comparator.comparingDouble(Ad::getPrice).reversed());
        } else if ("جدیدترین".equals(sortValue)) {
            sorted.sort(java.util.Comparator.comparing(Ad::getCreatedAt).reversed());
        }
        return sorted;
    }

    private void loadAds() {
        statusLabel.setText("⏳ در حال بارگذاری آگهی‌ها...");
        new Thread(() -> {
            try {
                Response<List<Ad>> response = apiClient.getActiveAds();
                Platform.runLater(() -> {
                    if (response.isSuccess() && response.getData() != null) {
                        List<Ad> ads = applySort(response.getData());
                        adListView.getItems().setAll(ads);
                        statusLabel.setText("✅ " + response.getData().size() + " آگهی فعال");
                    } else {
                        statusLabel.setText("❌ " + response.getMessage());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("❌ خطا در بارگذاری آگهی‌ها"));
            }
        }).start();
    }
    private void loadCategoriesAndCities() {
        new Thread(() -> {
            try {
                Response<List<Category>> catResponse = apiClient.getAllCategories();
                if (catResponse.isSuccess() && catResponse.getData() != null) {
                    Platform.runLater(() -> {
                        categoryCombo.getItems().setAll(catResponse.getData());
                    });
                }

                Response<List<City>> cityResponse = apiClient.getAllCities();
                if (cityResponse.isSuccess() && cityResponse.getData() != null) {
                    Platform.runLater(() -> {
                        cityCombo.getItems().setAll(cityResponse.getData());
                    });
                }
            } catch (Exception e) {
                System.err.println("Error loading categories/cities: " + e.getMessage());
            }
        }).start();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void show() {
        stage.setScene(scene);
        stage.setTitle("سامانه خرید و فروش - صفحه اصلی");
    }
}