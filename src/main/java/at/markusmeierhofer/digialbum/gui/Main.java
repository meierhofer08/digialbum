package at.markusmeierhofer.digialbum.gui;

import at.markusmeierhofer.digialbum.bl.config.settings.VTDSettings;
import at.markusmeierhofer.digialbum.bl.config.settings.VTDSettingsCreator;
import at.markusmeierhofer.digialbum.gui.mainpage.VTDController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private VTDController vtdController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        VTDSettingsCreator.setGuiAvailable(true);
        VTDSettings settings = VTDSettings.getInstance();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vtd.fxml"));
        primaryStage.setTitle(settings.getHeaderText());
        primaryStage.setScene(new Scene(loader.load(), 1000, 710));
        primaryStage.show();
        vtdController = loader.getController();
    }

    @Override
    public void stop() throws Exception {
        vtdController.shutdown();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
