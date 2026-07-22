package app.view.cell;

import app.dto.response.Response;
import app.client.ApiClient;
import app.model.Ad;
import app.view.page.AdDetailPage;
import app.view.page.FavoritesPage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;

public class FavoriteAdListCell extends ListCell<Ad> {
    private ApiClient apiClient;
    private Stage stage;
    private FavoritesPage favoritesPage;
    private VBox content;
    private Label titleLabel;
    private Label priceLabel;
    private Label descriptionLabel;
    private Label sellerLabel;
    private Label statusLabel;
    private Label dateLabel;
    private Button viewButton;
    private Button removeButton;

    public FavoriteAdListCell(ApiClient apiClient, Stage stage, FavoritesPage favoritesPage) {
        this.apiClient = apiClient;
        this.stage = stage;
        this.favoritesPage = favoritesPage;
        createCellContent();
    }

    private void createCellContent() {
        content = new VBox(8);
        content.setPadding(new Insets(12));
        content.setStyle("-fx-background-color: white; -fx-border-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 5, 0, 0, 2);");
        content.setPrefWidth(900);

        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        titleLabel = new Label();
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        priceLabel = new Label();
        priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e67e22;");

        titleRow.getChildren().addAll(titleLabel, spacer, priceLabel);

        descriptionLabel = new Label();
        descriptionLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
        descriptionLabel.setMaxWidth(800);
        descriptionLabel.setWrapText(true);

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

        removeButton = new Button("💔 حذف از علاقه‌مندی‌ها");
        removeButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5 15; -fx-border-radius: 5;");

        viewButton = new Button("🔍 مشاهده");
        viewButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 15; " +
                "-fx-border-radius: 5;");

        bottomRow.getChildren().addAll(sellerLabel, statusLabel, dateLabel, spacer2, removeButton, viewButton);

        content.getChildren().addAll(titleRow, descriptionLabel, bottomRow);
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

        String statusText;
        String statusColor;
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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        dateLabel.setText("📅 " + sdf.format(ad.getCreatedAt()));

        viewButton.setOnAction(e -> {
            AdDetailPage detailPage = new AdDetailPage(stage, apiClient, ad.getId());
            detailPage.show();
        });

        removeButton.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("تایید حذف");
            confirm.setHeaderText(null);
            confirm.setContentText("آیا می‌خواهید این آگهی را از علاقه‌مندی‌ها حذف کنید؟");
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

            try {
                Long userId = apiClient.getCurrentUser().getId();
                Response<Boolean> response = apiClient.removeFavorite(ad.getId(), userId);
                if (response.isSuccess()) {
                    favoritesPage.refreshFavorites();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText(response.getMessage());
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("خطا در حذف: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        content.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                AdDetailPage detailPage = new AdDetailPage(stage, apiClient, ad.getId());
                detailPage.show();
            }
        });

        setGraphic(content);
    }
}