package app.view.page;

import app.client.ApiClient;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainFX extends Application {

    private static ApiClient apiClient;

    @Override
    public void start(Stage primaryStage) {
        apiClient = new ApiClient("localhost", 8080);

        LoginPage loginPage = new LoginPage(primaryStage, apiClient);
        loginPage.show();

        primaryStage.setTitle("سامانه خرید و فروش دست دوم");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(750);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}