package app.view.cell;
import app.client.ApiClient;
import app.model.Conversation;
import app.model.User;
import app.view.ChatPage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;

public class ConversationListCell extends ListCell<Conversation> {
    private ApiClient apiClient;
    private Stage stage;
    private VBox content;
    private Label titleLabel;
    private Label withLabel;
    private Label lastMessageTimeLabel;
    private Label adIdLabel;
    private Button openButton;

    public ConversationListCell(ApiClient apiClient, Stage stage) {
        this.apiClient = apiClient;
        this.stage = stage;
        createCellContent();
    }

    private void createCellContent() {
        content = new VBox(5);
        content.setPadding(new Insets(12));
        content.setStyle("-fx-background-color: white; -fx-border-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        titleLabel = new Label();
        titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        lastMessageTimeLabel = new Label();
        lastMessageTimeLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12px;");

        topRow.getChildren().addAll(titleLabel, spacer, lastMessageTimeLabel);

        HBox bottomRow = new HBox(15);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        withLabel = new Label();
        withLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");

        adIdLabel = new Label();
        adIdLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        openButton = new Button("📂 باز کردن");
        openButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 20; " +
                "-fx-border-radius: 5;");

        bottomRow.getChildren().addAll(withLabel, adIdLabel, spacer2, openButton);

        content.getChildren().addAll(topRow, bottomRow);
    }

    @Override
    protected void updateItem(Conversation conversation, boolean empty) {
        super.updateItem(conversation, empty);

        if (empty || conversation == null) {
            setGraphic(null);
            return;
        }

        User currentUser = apiClient.getCurrentUser();
        if (currentUser == null) {
            setGraphic(null);
            return;
        }

        // Determine other person
        // Determine other person
        boolean isBuyer = currentUser.getId().equals(conversation.getBuyerId());

        String otherPersonName = isBuyer ?
                (conversation.getSellerFullName() != null ? conversation.getSellerFullName() : "کاربر " + conversation.getSellerId()) :
                (conversation.getBuyerFullName() != null ? conversation.getBuyerFullName() : "کاربر " + conversation.getBuyerId());

        String role = isBuyer ? "خریدار" : "فروشنده";
        String adTitle = conversation.getAdTitle() != null ? conversation.getAdTitle() : ("آگهی #" + conversation.getAdId());

        titleLabel.setText("💬 گفت‌وگو با " + otherPersonName);
        withLabel.setText("👤 نقش: " + role);
        adIdLabel.setText("📄 آگهی: " + adTitle);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        lastMessageTimeLabel.setText(sdf.format(conversation.getLastMessageTime()));

        openButton.setOnAction(e -> {
            ChatPage chatPage = new ChatPage(stage, apiClient, conversation.getId());
            chatPage.show();
        });

        // Double click to open
        content.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                ChatPage chatPage = new ChatPage(stage, apiClient, conversation.getId());
                chatPage.show();
            }
        });

        setGraphic(content);
    }
}