package at.markusmeierhofer.digialbum.gui;

import at.markusmeierhofer.digialbum.bl.constants.VTDConstants;
import at.markusmeierhofer.digialbum.gui.mainpage.VTDController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private VTDController vtdController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //VTDSettingsCreator.setGuiAvailable(true); Commented out until a way is found to display an alert on settings initialization
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VTDConstants.MAIN_FXML_NAME));
        primaryStage.setTitle(VTDConstants.APPLICATION_NAME);
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
