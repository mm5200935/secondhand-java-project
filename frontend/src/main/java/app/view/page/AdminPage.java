package app.view.page;

import app.client.ApiClient;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminPage {
    private Stage stage;
    private ApiClient apiClient;
    private Scene scene;

    public AdminPage(Stage stage, ApiClient apiClient) {
        this.stage = stage;
        this.apiClient = apiClient;
        this.scene = new Scene(new VBox(), 1000, 750);
    }

    public void show() {
        stage.setScene(scene);
        stage.setTitle("پنل ادمین");
    }
}