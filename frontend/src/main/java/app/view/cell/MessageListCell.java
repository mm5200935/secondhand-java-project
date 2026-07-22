package app.view.cell;
import app.model.Conversation;
import app.model.Message;
import app.model.User;
import app.client.ApiClient;
import app.view.page.ChatPage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;


import java.text.SimpleDateFormat;

public class MessageListCell extends ListCell<Message> {
    private ApiClient apiClient;
    private VBox content;
    private Label senderLabel;
    private Label messageLabel;
    private Label timeLabel;
    private ChatPage chatPage;

    public MessageListCell(ApiClient apiClient, ChatPage chatPage) {
        this.apiClient = apiClient;
        this.chatPage = chatPage;
        createCellContent();
    }

    private void createCellContent() {
        content = new VBox(5);
        content.setPadding(new Insets(8, 12, 8, 12));
        content.setStyle("-fx-background-color: white; -fx-border-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 3, 0, 0, 1);");
        content.setMaxWidth(700);

        HBox headerRow = new HBox(10);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        senderLabel = new Label();
        senderLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        timeLabel = new Label();
        timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #95a5a6;");

        headerRow.getChildren().addAll(senderLabel, spacer, timeLabel);

        messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
        messageLabel.setWrapText(true);

        content.getChildren().addAll(headerRow, messageLabel);
    }

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        if (empty || message == null) {
            setGraphic(null);
            return;
        }

        User currentUser = apiClient.getCurrentUser();
        boolean isOwnMessage = currentUser != null && currentUser.getId().equals(message.getSenderId());

        // Set sender name
        String senderName;
        if (isOwnMessage) {
            senderName = "شما";
        } else {
            Conversation conv = chatPage.getConversation();
            boolean currentUserIsBuyer = conv != null
                    && currentUser.getId().equals(conv.getBuyerId());
            senderName = currentUserIsBuyer ? "فروشنده" : "خریدار";
        }
        senderLabel.setText("👤 " + senderName);

        // Set message
        messageLabel.setText(message.getContent());

        // Set time
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        timeLabel.setText(sdf.format(message.getSentAt()));

        // Style based on sender
        if (isOwnMessage) {
            content.setStyle("-fx-background-color: #d4e6f1; -fx-border-radius: 10; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 3, 0, 0, 1);");
            content.setAlignment(Pos.CENTER_RIGHT);
        } else {
            content.setStyle("-fx-background-color: white; -fx-border-radius: 10; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 3, 0, 0, 1);");
            content.setAlignment(Pos.CENTER_LEFT);
        }

        setGraphic(content);
    }


}