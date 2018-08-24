package at.markusmeierhofer.digialbum.gui;

import at.markusmeierhofer.digialbum.bl.config.VTDConfig;
import at.markusmeierhofer.digialbum.bl.config.VTDSettingsCreator;
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
        VTDConfig config = VTDConfig.getInstance();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vtd.fxml"));
        primaryStage.setTitle(config.getHeaderText());
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
