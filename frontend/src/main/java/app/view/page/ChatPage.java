package app.view.page;

import app.client.ApiClient;
import app.dto.response.Response;
import app.model.Conversation;
import app.model.Message;
import app.model.User;
import app.view.cell.MessageListCell;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.List;

public class ChatPage {
    private Stage stage;
    private ApiClient apiClient;
    private Long conversationId;
    private Scene scene;
    private Conversation conversation;
    private ListView<Message> messageListView;
    private TextField messageField;
    private Button sendButton;
    private Label titleLabel;
    private Label lastMessageLabel;


    public ChatPage(Stage stage, ApiClient apiClient, Long conversationId) {
        this.stage = stage;
        this.apiClient = apiClient;
        this.conversationId = conversationId;
        createScene();
        loadConversation();
    }

    public Conversation getConversation() {
        return conversation;
    }

    private void createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f4f8;");

        // Top Bar
        root.setTop(createTopBar());

        // Center - Messages
        VBox centerContent = new VBox(10);
        centerContent.setPadding(new Insets(10));

        messageListView = new ListView<>();
        messageListView.setCellFactory(param -> new MessageListCell(apiClient, this));
        messageListView.setStyle("-fx-background-color: transparent; -fx-border: none;");
        VBox.setVgrow(messageListView, Priority.ALWAYS);
        messageListView.setPrefHeight(500);

        centerContent.getChildren().add(messageListView);

        // Bottom - Message input
        HBox inputBox = new HBox(10);
        inputBox.setPadding(new Insets(10));
        inputBox.setStyle("-fx-background-color: white; -fx-border-color: #dce1e8; -fx-border-width: 1 0 0 0;");
        inputBox.setAlignment(Pos.CENTER);

        messageField = new TextField();
        messageField.setPromptText("پیام خود را بنویسید...");
        messageField.setPrefWidth(600);
        messageField.setStyle("-fx-padding: 10; -fx-border-radius: 20; -fx-border-color: #dce1e8;");

        sendButton = new Button("📤 ارسال");
        sendButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10 30; " +
                "-fx-border-radius: 20;");
        sendButton.setOnAction(e -> sendMessage());

        inputBox.getChildren().addAll(messageField, sendButton);
        centerContent.getChildren().add(inputBox);

        ScrollPane scrollPane = new ScrollPane(centerContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f0f4f8; -fx-border: none;");
        root.setCenter(scrollPane);

        this.scene = new Scene(root, 1000, 750);

        // Enter key to send
        messageField.setOnAction(e -> sendMessage());
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
            ConversationsPage conversationsPage = new ConversationsPage(stage, apiClient);
            conversationsPage.show();
        });

        titleLabel = new Label("💬 گفت‌وگو");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        lastMessageLabel = new Label();
        lastMessageLabel.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 13px;");

        topBar.getChildren().addAll(backButton, titleLabel, spacer, lastMessageLabel);
        return topBar;
    }

    private void loadConversation() {
        try {
            User currentUser = apiClient.getCurrentUser();
            if (currentUser == null) {
                showAlert("خطا", "لطفاً ابتدا وارد شوید");
                return;
            }

            Response<Conversation> response = apiClient.getConversation(conversationId, currentUser.getId());
            if (response.isSuccess() && response.getData() != null) {
                this.conversation = response.getData();
                updateTitle();
                loadMessages();
            } else {
                showAlert("خطا", "خطا در دریافت اطلاعات گفت‌وگو: " + response.getMessage());
            }
        } catch (Exception e) {
            showAlert("خطا", "خطا در ارتباط با سرور: " + e.getMessage());
        }
    }

    private void updateTitle() {
        User currentUser = apiClient.getCurrentUser();
        if (currentUser == null || conversation == null) return;

        boolean isBuyer = currentUser.getId().equals(conversation.getBuyerId());

        String otherPersonName = isBuyer ?
                (conversation.getSellerFullName() != null ? conversation.getSellerFullName() : "کاربر " + conversation.getSellerId()) :
                (conversation.getBuyerFullName() != null ? conversation.getBuyerFullName() : "کاربر " + conversation.getBuyerId());

        titleLabel.setText("💬 گفت‌وگو با " + otherPersonName);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        lastMessageLabel.setText("آخرین پیام: " + sdf.format(conversation.getLastMessageTime()));
    }

    private void loadMessages() {
        try {
            User currentUser = apiClient.getCurrentUser();
            if (currentUser == null) return;

            Response<List<Message>> response = apiClient.getConversationMessages(conversationId, currentUser.getId());
            if (response.isSuccess() && response.getData() != null) {
                messageListView.getItems().setAll(response.getData());
                // Scroll to bottom
                if (!messageListView.getItems().isEmpty()) {
                    messageListView.scrollTo(messageListView.getItems().size() - 1);
                }
            }
        } catch (Exception e) {
            showAlert("خطا", "خطا در دریافت پیام‌ها: " + e.getMessage());
        }
    }

    private void sendMessage() {
        String content = messageField.getText().trim();
        if (content.isEmpty()) {
            return;
        }

        try {
            User currentUser = apiClient.getCurrentUser();
            if (currentUser == null) {
                showAlert("خطا", "لطفاً ابتدا وارد شوید");
                return;
            }

            Response<Message> response = apiClient.sendMessageToConversation(conversationId, content);
            if (response.isSuccess()) {
                messageField.clear();
                loadMessages();
            } else {
                showAlert("خطا", response.getMessage());
            }
        } catch (Exception e) {
            showAlert("خطا", "خطا در ارسال پیام: " + e.getMessage());
        }
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
        stage.setTitle("گفت‌وگو");
    }
}


