package app;

import app.controller.ApiController;

public class App {
    public static void main(String[] args) {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Using default port 8080");
            }
        }

        ApiController controller = new ApiController();
        controller.startServer(port);
    }
}