package at.markusmeierhofer.digialbum;

import at.markusmeierhofer.digialbum.config.VTDConfig;
import at.markusmeierhofer.digialbum.config.VTDSettingsCreator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        VTDSettingsCreator.setGuiAvailable(true);
        VTDConfig config = VTDConfig.getInstance();
        Parent root = FXMLLoader.load(getClass().getResource("/vtd.fxml"));
        primaryStage.setTitle(config.getHeaderText());
        primaryStage.setScene(new Scene(root, 1000, 710));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
