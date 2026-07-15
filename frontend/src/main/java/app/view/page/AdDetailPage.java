package app.view.page;

import app.dto.request.MessageRequest;
import app.dto.request.RatingRequest;
import app.dto.response.Response;
import app.client.ApiClient;
import app.model.Ad;
import app.model.Category;
import app.model.City;
import app.model.Conversation;
import app.model.Rating;
import app.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdDetailPage {
    private Stage stage;
    private ApiClient apiClient;
    private Long adId;
    private Scene scene;
    private Ad ad;
    private Label titleLabel;
    private Label priceLabel;
    private Label sellerLabel;
    private Label ratingLabel;
    private Label categoryLabel;
    private Label cityLabel;
    private Label dateLabel;
    private Label statusLabel;
    private TextArea descriptionArea;
    private ListView<String> imagesListView;
    private Button favoriteButton;
    private Button messageButton;
    private Button editButton;
    private Button deleteButton;
    private Button soldButton;
    private Button rateButton;
    private Label messageLabel;
    private VBox imagesContainer;
    private VBox reviewsBox;
    private Runnable onBack;

    public AdDetailPage(Stage stage, ApiClient apiClient, Long adId) {
        this(stage, apiClient, adId, null);   // رفتار قبلی حفظ می‌شه
    }

    public AdDetailPage(Stage stage, ApiClient apiClient, Long adId, Runnable onBack) {
        this.stage = stage;
        this.apiClient = apiClient;
        this.adId = adId;
        this.onBack = onBack;
        createScene();
        loadAdDetails();
    }



    private void createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f4f8;");

        // Top Bar
        root.setTop(createTopBar());

        // Center - Content
        ScrollPane scrollPane = new ScrollPane(createContent());
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
            if (onBack != null) {
                onBack.run();
            } else {
                HomePage homePage = new HomePage(stage, apiClient);
                homePage.show();
            }
        });

        Label titleLabel = new Label("📄 جزئیات آگهی");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Favorite button in top bar
        favoriteButton = new Button("⭐ افزودن به علاقه‌مندی‌ها");
        favoriteButton.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: #2c3e50; -fx-padding: 8 15; " +
                "-fx-border-radius: 5;");
        favoriteButton.setOnAction(e -> toggleFavorite());

        boolean isAdminUser = apiClient.getCurrentUser() != null && apiClient.getCurrentUser().isAdmin();
        if (isAdminUser) {
            topBar.getChildren().addAll(backButton, titleLabel, spacer);
        } else {
            topBar.getChildren().addAll(backButton, titleLabel, spacer, favoriteButton);
        }
        return topBar;
    }

    private VBox createContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        // Loading indicator
        Label loadingLabel = new Label("⏳ در حال بارگذاری...");
        loadingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
        content.getChildren().add(loadingLabel);

        return content;
    }

    private String getCategoryName(Long categoryId) {
        if (categoryId == null) {
            return "نامشخص";
        }
        try {
            Response<List<Category>> response = apiClient.getAllCategories();
            if (response.isSuccess() && response.getData() != null) {
                for (Category category : response.getData()) {
                    if (categoryId.equals(category.getId())) {
                        return category.getName();
                    }
                }
            }
        } catch (Exception e) {
            // در صورت بروز خطا، مقدار پیش‌فرض بازگردانده می‌شود
        }
        return "نامشخص";
    }

    private String getCityName(Long cityId) {
        if (cityId == null) {
            return "نامشخص";
        }
        try {
            Response<List<City>> response = apiClient.getAllCities();
            if (response.isSuccess() && response.getData() != null) {
                for (City city : response.getData()) {
                    if (cityId.equals(city.getId())) {
                        return city.getName();
                    }
                }
            }
        } catch (Exception e) {
            // در صورت بروز خطا، مقدار پیش‌فرض بازگردانده می‌شود
        }
        return "نامشخص";
    }

    private void loadAdDetails() {
        try {
            Response<Ad> response = apiClient.getAdDetails(adId);
            if (response.isSuccess() && response.getData() != null) {
                this.ad = response.getData();
                updateContent();
                if (apiClient.getCurrentUser() == null || !apiClient.getCurrentUser().isAdmin()) {
                    checkFavoriteStatus();
                }
                checkUserRating();
            } else {
                showError("خطا در دریافت اطلاعات آگهی: " + response.getMessage());
            }
        } catch (Exception e) {
            showError("خطا در ارتباط با سرور: " + e.getMessage());
        }
    }

    private void updateContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        // Title and Price
        HBox titleRow = new HBox(15);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        titleLabel = new Label(ad.getTitle());
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        priceLabel = new Label(String.format("💰 %,.0f تومان", ad.getPrice()));
        priceLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #e67e22;");

        titleRow.getChildren().addAll(titleLabel, spacer, priceLabel);
        content.getChildren().add(titleRow);

        // Status
        statusLabel = new Label();
        String statusText = "";
        String statusColor = "";
        switch (ad.getStatus()) {
            case ACTIVE:
                statusText = "✅ فعال";
                statusColor = "#2ecc71";
                break;
            case PENDING:
                statusText = "⏳ در انتظار بررسی";
                statusColor = "#f39c12";
                break;
            case SOLD:
                statusText = "💰 فروخته شده";
                statusColor = "#e74c3c";
                break;
            case REJECTED:
                statusText = "❌ رد شده";
                statusColor = "#e74c3c";
                break;
            case DELETED:
                statusText = "🗑️ حذف شده";
                statusColor = "#95a5a6";
                break;
        }
        statusLabel.setText(statusText);
        statusLabel.setStyle("-fx-text-fill: " + statusColor + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        content.getChildren().add(statusLabel);

        // Images
        if (ad.getImagePaths() != null && !ad.getImagePaths().isEmpty()) {
            imagesContainer = new VBox(10);
            imagesContainer.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-radius: 8;");

            Label imagesLabel = new Label("📷 تصاویر آگهی");
            imagesLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            imagesContainer.getChildren().add(imagesLabel);

            HBox imagesRow = new HBox(10);
            imagesRow.setAlignment(Pos.CENTER_LEFT);

            for (String path : ad.getImagePaths()) {
                ImageView imageView = createImageView(path);
                imagesRow.getChildren().add(imageView);
            }

            imagesContainer.getChildren().add(imagesRow);
            content.getChildren().add(imagesContainer);
        }

        // Description
        VBox descBox = new VBox(10);
        descBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");

        Label descLabel = new Label("📝 توضیحات");
        descLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        descriptionArea = new TextArea(ad.getDescription());
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefHeight(150);
        descriptionArea.setStyle("-fx-background-color: #f8f9fa; -fx-border: none;");

        descBox.getChildren().addAll(descLabel, descriptionArea);
        content.getChildren().add(descBox);

        // Info Grid
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(20);
        infoGrid.setVgap(10);
        infoGrid.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");

        // Seller info
        sellerLabel = new Label("👤 فروشنده: " + (ad.getOwnerFullName() != null ? ad.getOwnerFullName() : ad.getOwnerId()));
        sellerLabel.setStyle("-fx-font-size: 14px;");
        infoGrid.add(sellerLabel, 0, 0);

        // Rating
        ratingLabel = new Label("⭐ در حال بارگذاری...");
        ratingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #f39c12;");
        infoGrid.add(ratingLabel, 1, 0);
        loadAdRating(ad.getId());

        // Category
        categoryLabel = new Label("📂 دسته‌بندی: " + getCategoryName(ad.getCategoryId()));
        categoryLabel.setStyle("-fx-font-size: 14px;");
        infoGrid.add(categoryLabel, 0, 1);

        // City
        cityLabel = new Label("📍 شهر: " + getCityName(ad.getCityId()));
        cityLabel.setStyle("-fx-font-size: 14px;");
        infoGrid.add(cityLabel, 1, 1);

        // Date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        dateLabel = new Label("📅 تاریخ ثبت: " + sdf.format(ad.getCreatedAt()));
        dateLabel.setStyle("-fx-font-size: 14px;");
        infoGrid.add(dateLabel, 0, 2);

        // Update date
        if (ad.getUpdatedAt() != null) {
            Label updateLabel = new Label("🔄 آخرین بروزرسانی: " + sdf.format(ad.getUpdatedAt()));
            updateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            infoGrid.add(updateLabel, 1, 2);
        }

        content.getChildren().add(infoGrid);

        // Reviews section
        reviewsBox = new VBox(10);
        reviewsBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-radius: 8;");
//        Label reviewsTitle = new Label("💬 نظرات خریداران");
//        reviewsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
//        reviewsBox.getChildren().add(reviewsTitle);
        loadAdReviews(reviewsBox);
        content.getChildren().add(reviewsBox);

        // Action Buttons
        User currentUser = apiClient.getCurrentUser();
        boolean isOwner = currentUser != null && currentUser.getId().equals(ad.getOwnerId());
        boolean isAdmin = currentUser != null && currentUser.isAdmin();
        boolean isPendingReview = ad.getStatus() == Ad.AdStatus.PENDING;

        // اگر ادمین دارد آگهیِ در انتظار بررسی را می‌بیند، کلاً بخش عملیات نمایش داده نشود
        if (!(isAdmin && isPendingReview)) {

            VBox actionBox = new VBox(10);
            actionBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-radius: 8;");

            Label actionLabel = new Label("🔧 عملیات");
            actionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            actionBox.getChildren().add(actionLabel);

            HBox actionButtons = new HBox(10);
            actionButtons.setAlignment(Pos.CENTER_LEFT);

            // Message button (for buyers)
            messageButton = new Button("💬 پیام به فروشنده");
            messageButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 20; " +
                    "-fx-border-radius: 5;");
            messageButton.setOnAction(e -> openChatWithSeller());

            // Rate button
            rateButton = new Button("⭐ امتیازدهی");
            rateButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 8 20; " +
                    "-fx-border-radius: 5;");
            rateButton.setOnAction(e -> showRatingDialog());

            // Delete button (for owner or admin)
            deleteButton = new Button("🗑️ حذف");
            deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8 20; " +
                    "-fx-border-radius: 5;");
            deleteButton.setOnAction(e -> deleteAd());

            // Sold button (for owner)
            soldButton = new Button("💰 فروخته شد");
            soldButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-padding: 8 20; " +
                    "-fx-border-radius: 5;");
            soldButton.setOnAction(e -> markAsSold());

            // Show appropriate buttons
            if (isOwner) {
                actionButtons.getChildren().addAll(soldButton, deleteButton);
            } else if (isAdmin) {
                actionButtons.getChildren().add(deleteButton);
            } else {
                // Regular user viewing someone else's ad
                if (ad.getStatus() == Ad.AdStatus.ACTIVE || ad.getStatus() == Ad.AdStatus.SOLD) {
                    actionButtons.getChildren().addAll(messageButton, rateButton);
                }
            }

            actionBox.getChildren().add(actionButtons);
            content.getChildren().add(actionBox);
        }

        // Message label
        messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 13px;");
        content.getChildren().add(messageLabel);

        // Replace content
        BorderPane root = (BorderPane) scene.getRoot();
        ScrollPane scrollPane = (ScrollPane) root.getCenter();
        scrollPane.setContent(content);

        // Update favorite button text
        updateFavoriteButton();
    }

    private ImageView createImageView(String path) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-border-radius: 5; -fx-border-color: #dce1e8;");

        try {
            File file = new File(path);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
            } else {
                // Try as resource
                Image image = new Image(getClass().getResourceAsStream(path));
                if (image != null) {
                    imageView.setImage(image);
                }
            }
        } catch (Exception e) {
            // Image not found, show placeholder
            imageView.setStyle("-fx-background-color: #ecf0f1; -fx-border-radius: 5; -fx-border-color: #dce1e8;");
        }

        return imageView;
    }

    private void openChatWithSeller() {
        User currentUser = apiClient.getCurrentUser();
        if (currentUser == null) {
            showMessage("⚠️ لطفاً ابتدا وارد شوید", "red");
            return;
        }
        if ("BLOCKED".equals(ad.getOwnerStatus())) {
            showMessage("🚫 کاربر مسدود شده است", "red");
            return;
        }

        try {
            Response<Conversation> response = apiClient.startConversation(adId);
            if (response.isSuccess() && response.getData() != null) {
                ChatPage chatPage = new ChatPage(stage, apiClient, response.getData().getId());
                chatPage.show();
            } else {
                showMessage("❌ " + response.getMessage(), "red");
            }
        } catch (Exception e) {
            showMessage("❌ خطا در باز کردن گفت‌وگو: " + e.getMessage(), "red");
        }
    }

    private void loadSellerRating(Long sellerId) {
        try {
            Response<Double> response = apiClient.getSellerAverageRating(sellerId);
            if (response.isSuccess() && response.getData() != null) {
                ratingLabel.setText(String.format("⭐ %.1f / 5.0", response.getData()));
            } else {
                ratingLabel.setText("⭐ بدون امتیاز");
            }
        } catch (Exception e) {
            ratingLabel.setText("⭐ خطا در دریافت امتیاز");
        }
    }

    private void loadAdRating(Long adId) {
        try {
            Response<List<Rating>> response = apiClient.getAdRatings(adId);
            if (response.isSuccess() && response.getData() != null && !response.getData().isEmpty()) {
                List<Rating> ratings = response.getData();
                double sum = 0;
                for (Rating r : ratings) {
                    sum += r.getScore();
                }
                double average = sum / ratings.size();
                ratingLabel.setText(String.format("⭐ %.1f / 5.0 (%d نظر)", average, ratings.size()));
            } else {
                ratingLabel.setText("⭐ بدون امتیاز");
            }
        } catch (Exception e) {
            ratingLabel.setText("⭐ خطا در دریافت امتیاز");
        }
    }

    private void checkFavoriteStatus() {
        try {
            User currentUser = apiClient.getCurrentUser();
            if (currentUser != null) {
                Response<Boolean> response = apiClient.isFavorite(adId, currentUser.getId());
                if (response.isSuccess() && response.getData() != null) {
                    if (response.getData()) {
                        favoriteButton.setText("⭐ در علاقه‌مندی‌ها");
                        favoriteButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; " +
                                "-fx-padding: 8 15; -fx-border-radius: 5;");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking favorite: " + e.getMessage());
        }
    }

    private void checkUserRating() {
        try {
            User currentUser = apiClient.getCurrentUser();
            if (currentUser != null && !currentUser.getId().equals(ad.getOwnerId())) {
                Response<Boolean> response = apiClient.hasUserRatedAd(adId, currentUser.getId());
                if (response.isSuccess() && response.getData() != null && response.getData()) {
                    rateButton.setDisable(true);
                    rateButton.setText("✅ قبلاً امتیاز داده‌اید");
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking rating: " + e.getMessage());
        }
    }

    private void updateFavoriteButton() {
        User currentUser = apiClient.getCurrentUser();
        if (currentUser == null) return;

        try {
            Response<Boolean> response = apiClient.isFavorite(adId, currentUser.getId());
            if (response.isSuccess() && response.getData() != null && response.getData()) {
                favoriteButton.setText("⭐ در علاقه‌مندی‌ها");
                favoriteButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; " +
                        "-fx-padding: 8 15; -fx-border-radius: 5;");
            } else {
                favoriteButton.setText("⭐ افزودن به علاقه‌مندی‌ها");
                favoriteButton.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: #2c3e50; " +
                        "-fx-padding: 8 15; -fx-border-radius: 5;");
            }
        } catch (Exception e) {
            System.err.println("Error updating favorite button: " + e.getMessage());
        }
    }

    private void toggleFavorite() {
        try {
            User currentUser = apiClient.getCurrentUser();
            if (currentUser == null) {
                showMessage("⚠️ لطفاً ابتدا وارد شوید", "red");
                return;
            }

            Response<Boolean> checkResponse = apiClient.isFavorite(adId, currentUser.getId());
            boolean isFavorite = checkResponse.isSuccess() && checkResponse.getData() != null && checkResponse.getData();

            Response<Boolean> response;
            if (isFavorite) {
                response = apiClient.removeFavorite(adId, currentUser.getId());
            } else {
                response = apiClient.addFavorite(adId, currentUser.getId());
            }

            if (response.isSuccess()) {
                updateFavoriteButton();
                showMessage("✅ " + response.getMessage(), "green");
            } else {
                showMessage("❌ " + response.getMessage(), "red");
            }
        } catch (Exception e) {
            showMessage("❌ خطا: " + e.getMessage(), "red");
        }
    }

    private void sendMessage() {
        User currentUser = apiClient.getCurrentUser();
        if (currentUser == null) {
            showMessage("⚠️ لطفاً ابتدا وارد شوید", "red");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("ارسال پیام");
        dialog.setHeaderText("💬 ارسال پیام به فروشنده");
        dialog.setContentText("متن پیام خود را وارد کنید:");
        dialog.getEditor().setPrefWidth(400);

        dialog.showAndWait().ifPresent(content -> {
            if (content.trim().isEmpty()) {
                showMessage("⚠️ لطفاً متن پیام را وارد کنید", "red");
                return;
            }

            try {
                MessageRequest request = new MessageRequest();
                request.setAdId(adId);
                request.setSenderId(currentUser.getId());
                request.setContent(content.trim());

                Response<Conversation> response = apiClient.sendMessage(request);
                if (response.isSuccess()) {
                    showMessage("✅ پیام با موفقیت ارسال شد", "green");

                    // Navigate to chat
                    Conversation conv = response.getData();
                    ChatPage chatPage = new ChatPage(stage, apiClient, conv.getId());
                    chatPage.show();
                } else {
                    showMessage("❌ " + response.getMessage(), "red");
                }
            } catch (Exception e) {
                showMessage("❌ خطا در ارسال پیام: " + e.getMessage(), "red");
            }
        });
    }

    private void showRatingDialog() {
        User currentUser = apiClient.getCurrentUser();
        if (currentUser == null) {
            showMessage("⚠️ لطفاً ابتدا وارد شوید", "red");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("امتیازدهی به فروشنده");
        dialog.setHeaderText("⭐ امتیاز خود را به فروشنده بدهید");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(350);

        Label scoreLabel = new Label("امتیاز (1 تا 5):");
        scoreLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        ComboBox<Integer> scoreCombo = new ComboBox<>();
        scoreCombo.getItems().addAll(1, 2, 3, 4, 5);
        scoreCombo.setValue(5);
        scoreCombo.setPrefWidth(100);

        Label commentLabel = new Label("نظر (اختیاری):");
        commentLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextArea commentArea = new TextArea();
        commentArea.setPromptText("نظر خود را وارد کنید...");
        commentArea.setPrefHeight(80);
        commentArea.setWrapText(true);

        content.getChildren().addAll(scoreLabel, scoreCombo, commentLabel, commentArea);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                int score = scoreCombo.getValue() != null ? scoreCombo.getValue() : 5;
                String comment = commentArea.getText().trim();

                try {
                    RatingRequest request = new RatingRequest();
                    request.setAdId(adId);
                    request.setRaterId(currentUser.getId());
                    request.setScore(score);
                    request.setComment(comment);

                    Response<Rating> response = apiClient.rateSeller(request);
                    if (response.isSuccess()) {
                        showMessage("✅ امتیاز با موفقیت ثبت شد", "green");
                        rateButton.setDisable(true);
                        rateButton.setText("✅ قبلاً امتیاز داده‌اید");
                        loadAdRating(ad.getId());
                        loadAdReviews(reviewsBox);
                    } else {
                        showMessage("❌ " + response.getMessage(), "red");
                    }
                } catch (Exception e) {
                    showMessage("❌ خطا در ثبت امتیاز: " + e.getMessage(), "red");
                }
            }
        });
    }

    private void editAd() {
        // Implementation for editing ad
        // You can create an EditAdPage similar to NewAdPage with pre-filled data
        showMessage("✏️ قابلیت ویرایش در حال توسعه است", "orange");
    }

    private void deleteAd() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("تایید حذف");
        confirm.setHeaderText(null);
        confirm.setContentText("آیا مطمئن هستید که می‌خواهید این آگهی را حذف کنید؟");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                User currentUser = apiClient.getCurrentUser();
                Response<Ad> response = apiClient.deleteAd(adId, currentUser.getId());
                if (response.isSuccess()) {
                    showMessage("✅ آگهی با موفقیت حذف شد", "green");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("حذف آگهی");
                    alert.setHeaderText(null);
                    alert.setContentText("آگهی با موفقیت حذف شد.");
                    alert.showAndWait();

                    HomePage homePage = new HomePage(stage, apiClient);
                    homePage.show();
                } else {
                    showMessage("❌ " + response.getMessage(), "red");
                }
            } catch (Exception e) {
                showMessage("❌ خطا: " + e.getMessage(), "red");
            }
        }
    }

    private void markAsSold() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("تایید فروش");
        confirm.setHeaderText(null);
        confirm.setContentText("آیا مطمئن هستید که این آگهی فروخته شده است؟");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                User currentUser = apiClient.getCurrentUser();
                Response<Ad> response = apiClient.markAsSold(adId, currentUser.getId());
                if (response.isSuccess()) {
                    showMessage("✅ وضعیت آگهی به فروخته شده تغییر کرد", "green");
                    loadAdDetails(); // Reload page
                } else {
                    showMessage("❌ " + response.getMessage(), "red");
                }
            } catch (Exception e) {
                showMessage("❌ خطا: " + e.getMessage(), "red");
            }
        }
    }

    private void showMessage(String message, String color) {
        if (messageLabel != null) {
            messageLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 13px;");
            messageLabel.setText(message);
        }
    }

    private void showError(String message) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(50));
        content.setAlignment(Pos.CENTER);

        Label errorLabel = new Label("❌ " + message);
        errorLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #e74c3c;");

        Button backButton = new Button("← بازگشت به صفحه اصلی");
        backButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10 20;");
        backButton.setOnAction(e -> {
            if (onBack != null) {
                onBack.run();
            } else {
                HomePage homePage = new HomePage(stage, apiClient);
                homePage.show();
            }
        });

        content.getChildren().addAll(errorLabel, backButton);

        BorderPane root = (BorderPane) scene.getRoot();
        ScrollPane scrollPane = (ScrollPane) root.getCenter();
        scrollPane.setContent(content);
    }

    public void show() {
        stage.setScene(scene);
        stage.setTitle("جزئیات آگهی");
    }

    private void loadAdReviews(VBox reviewsBox) {
        reviewsBox.getChildren().clear();

        Label reviewsTitle = new Label("💬 نظرات کاربران");
        reviewsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        reviewsBox.getChildren().add(reviewsTitle);
        try {
            Response<List<Rating>> response = apiClient.getAdRatings(ad.getId());
            if (response.isSuccess() && response.getData() != null && !response.getData().isEmpty()) {
                for (Rating r : response.getData()) {
                    String name = r.getRaterFullName() != null ? r.getRaterFullName() : ("کاربر #" + r.getRaterId());
                    Label reviewLabel = new Label("👤 " + name + " (⭐ " + r.getScore() + "/5): " + r.getComment());
                    reviewLabel.setWrapText(true);
                    reviewLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e;");
                    reviewsBox.getChildren().add(reviewLabel);
                }
            } else {
                Label noReview = new Label("هنوز نظری برای این آگهی ثبت نشده است.");
                noReview.setStyle("-fx-font-size: 13px; -fx-text-fill: #95a5a6;");
                reviewsBox.getChildren().add(noReview);
            }
        } catch (Exception e) {
            Label errorLabel = new Label("خطا در دریافت نظرات.");
            errorLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #e74c3c;");
            reviewsBox.getChildren().add(errorLabel);
        }
    }
}