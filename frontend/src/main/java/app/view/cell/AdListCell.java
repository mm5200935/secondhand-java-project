package app.view.cell;

import app.client.ApiClient;
import app.model.Ad;
import app.view.page.AdDetailPage;
import app.view.page.EditAdPage;
import app.view.page.HomePage;
import app.view.page.MyAdsPage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;

public class AdListCell extends ListCell<Ad> {
    private ApiClient apiClient;
    private Stage stage;
    private VBox content;
    private Label titleLabel;
    private Label priceLabel;
    private Label descriptionLabel;
    private Label sellerLabel;
    private Label statusLabel;
    private Label dateLabel;
    private Button viewButton;
    private Button editButton; // کنار private Button viewButton; اضافه کن
    private HomePage homePage;
    private MyAdsPage myAdsPage;
    private Label rejectionLabel;

    public AdListCell(ApiClient apiClient, Stage stage) {
        this(apiClient, stage, null, null);
    }

    public AdListCell(ApiClient apiClient, Stage stage, HomePage homePage) {
        this(apiClient, stage, homePage, null);
    }

    public AdListCell(ApiClient apiClient, Stage stage, MyAdsPage myAdsPage) {
        this(apiClient, stage, null, myAdsPage);
    }

    private AdListCell(ApiClient apiClient, Stage stage, HomePage homePage, MyAdsPage myAdsPage) {
        this.apiClient = apiClient;
        this.stage = stage;
        this.homePage = homePage;
        this.myAdsPage = myAdsPage;
        createCellContent();
    }

    private void createCellContent() {
        content = new VBox(8);
        content.setPadding(new Insets(12));
        content.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-text-fill: #2c3e50; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 5, 0, 0, 2);");
        content.setPrefWidth(900);

        // Title row
        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        titleLabel = new Label();
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        priceLabel = new Label();
        priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e67e22;");

        titleRow.getChildren().addAll(titleLabel, spacer, priceLabel);

        // Description
        descriptionLabel = new Label();
        descriptionLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
        descriptionLabel.setMaxWidth(800);
        descriptionLabel.setWrapText(true);

        // Bottom row
        HBox bottomRow = new HBox(15);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        sellerLabel = new Label();
        sellerLabel.setStyle("-fx-text-fill: #34495e; -fx-font-size: 12px;");

        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 12px; -fx-font-weight: bold;");

        dateLabel = new Label();
        dateLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12px;");



        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        editButton = new Button("✏️ ویرایش");
        editButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 5 15; -fx-border-radius: 5;");
        bottomRow.getChildren().add(editButton);

        viewButton = new Button("🔍 مشاهده");
        viewButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 15; " +
                "-fx-border-radius: 5;");

        bottomRow.getChildren().addAll(sellerLabel, statusLabel, dateLabel, spacer2, viewButton);

        content.getChildren().addAll(titleRow, descriptionLabel, bottomRow);

        rejectionLabel = new Label();
        rejectionLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px; -fx-font-weight: bold;");
        rejectionLabel.setWrapText(true);
        rejectionLabel.setVisible(false);
        rejectionLabel.setManaged(false);
        content.getChildren().add(rejectionLabel);
    }

    @Override
    protected void updateItem(Ad ad, boolean empty) {
        super.updateItem(ad, empty);

        if (empty || ad == null) {
            setGraphic(null);
            return;
        }

        titleLabel.setText(ad.getTitle());
        priceLabel.setText(String.format("💰 %,.0f تومان", ad.getPrice()));

        String desc = ad.getDescription();
        if (desc.length() > 100) {
            desc = desc.substring(0, 100) + "...";
        }
        descriptionLabel.setText(desc);

        sellerLabel.setText("👤 فروشنده: " + (ad.getOwnerFullName() != null ? ad.getOwnerFullName() : ad.getOwnerId()));

        // Status
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
                statusColor = "#e74c3c";
                break;
            default:
                statusText = ad.getStatus().toString();
                statusColor = "#7f8c8d";
        }
        statusLabel.setText(statusText);
        statusLabel.setStyle("-fx-text-fill: " + statusColor + "; -fx-font-size: 12px; -fx-font-weight: bold;");

        if (ad.getStatus() == Ad.AdStatus.REJECTED && ad.getRejectionReason() != null && !ad.getRejectionReason().isBlank()) {
            rejectionLabel.setText("❌ دلیل رد: " + ad.getRejectionReason());
            rejectionLabel.setVisible(true);
            rejectionLabel.setManaged(true);
        } else {
            rejectionLabel.setVisible(false);
            rejectionLabel.setManaged(false);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        dateLabel.setText("📅 " + sdf.format(ad.getCreatedAt()));

        Long currentUserId = apiClient.getCurrentUser() != null ? apiClient.getCurrentUser().getId() : null;
        boolean canEdit = currentUserId != null && ad.isEditableByUser(currentUserId);
        editButton.setVisible(canEdit);
        editButton.setManaged(canEdit);
        editButton.setOnAction(e -> {
            EditAdPage editPage = new EditAdPage(stage, apiClient, ad);
            editPage.show();
        });

        viewButton.setOnAction(e -> {
            AdDetailPage detailPage;
            if (homePage != null) {
                detailPage = new AdDetailPage(stage, apiClient, ad.getId(), () -> homePage.show());
            } else if (myAdsPage != null) {
                detailPage = new AdDetailPage(stage, apiClient, ad.getId(), () -> myAdsPage.show());
            } else {
                detailPage = new AdDetailPage(stage, apiClient, ad.getId());
            }
            detailPage.show();
        });

        // Mouse click on cell
        content.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                AdDetailPage detailPage;
                if (homePage != null) {
                    detailPage = new AdDetailPage(stage, apiClient, ad.getId(), () -> homePage.show());
                } else if (myAdsPage != null) {
                    detailPage = new AdDetailPage(stage, apiClient, ad.getId(), () -> myAdsPage.show());
                } else {
                    detailPage = new AdDetailPage(stage, apiClient, ad.getId());
                }
                detailPage.show();
            }
        });

        setGraphic(content);
    }
}