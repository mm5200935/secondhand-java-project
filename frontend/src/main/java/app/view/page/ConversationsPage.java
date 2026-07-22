package app.view.page;

import app.client.ApiClient;
import app.dto.response.Response;
import app.model.Conversation;
import app.model.User;
import app.view.cell.ConversationListCell;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class ConversationsPage {
    private Stage stage;
    private ApiClient apiClient;
    private Scene scene;
    private ListView<Conversation> conversationListView;
    private Label statusLabel;

    public ConversationsPage(Stage stage, ApiClient apiClient) {
        this.stage = stage;
        this.apiClient = apiClient;
        createScene();
        loadConversations();
    }

    private void createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f4f8;");

        // Top Bar
        root.setTop(createTopBar());

        // Center
        VBox centerContent = new VBox(15);
        centerContent.setPadding(new Insets(20));

        Label headerLabel = new Label("💬 گفت‌وگوهای من");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        centerContent.getChildren().add(headerLabel);

        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
        centerContent.getChildren().add(statusLabel);

        conversationListView = new ListView<>();
        conversationListView.setCellFactory(param -> new ConversationListCell(apiClient, stage));
        conversationListView.setStyle("-fx-background-color: transparent; -fx-border: none;");
        VBox.setVgrow(conversationListView, Priority.ALWAYS);
        conversationListView.setPrefHeight(500);

        centerContent.getChildren().add(conversationListView);

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

        Label titleLabel = new Label("💬 پیام‌ها");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshButton = new Button("🔄 بروزرسانی");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15; " +
                "-fx-border-radius: 5;");
        refreshButton.setOnAction(e -> loadConversations());

        topBar.getChildren().addAll(backButton, titleLabel, spacer, refreshButton);
        return topBar;
    }

    private void loadConversations() {
        try {
            User currentUser = apiClient.getCurrentUser();
            if (currentUser == null) {
                statusLabel.setText("⚠️ لطفاً ابتدا وارد شوید");
                return;
            }

            Response<List<Conversation>> response = apiClient.getConversations(currentUser.getId());
            if (response.isSuccess() && response.getData() != null) {
                conversationListView.getItems().setAll(response.getData());
                statusLabel.setText("📋 " + response.getData().size() + " گفت‌وگو");
            } else {
                statusLabel.setText("❌ " + response.getMessage());
            }
        } catch (Exception e) {
            statusLabel.setText("❌ خطا در دریافت گفت‌وگوها: " + e.getMessage());
        }
    }

    public void show() {
        stage.setScene(scene);
        stage.setTitle("پیام‌ها");
    }
}


