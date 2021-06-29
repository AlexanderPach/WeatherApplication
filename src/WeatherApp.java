import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;


public class WeatherApp extends Application {

    /**
     * GUI entry point
     *
     * @param primaryStage Main window of the application.
     */
    @Override public void start(Stage primaryStage) {
        try {
            URL url = getClass().getResource("fxml_weather.fxml");
            BorderPane root = FXMLLoader.load(url);
            Scene scene = new Scene(root, 1000, 800);
            primaryStage.setTitle("Weather by County");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Application entry point.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
