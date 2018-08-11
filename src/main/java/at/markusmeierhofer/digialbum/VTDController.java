package at.markusmeierhofer.digialbum;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class VTDController {
    public final static int IMAGE_CONTAINER_WIDTH = 1024;
    public final static int IMAGE_CONTAINER_HEIGHT = 768;
    public final static int IMAGE_FIT_WIDTH = 480;
    public final static int IMAGE_FIT_HEIGHT = 425;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView rightImageview;

    @FXML
    private Label leftDateLabel;

    @FXML
    private ImageView leftImageview;

    @FXML
    private TextArea rightTextarea;

    @FXML
    private Button closeBtn;

    @FXML
    private Button backBtn;

    @FXML
    private Button settingsBtn;

    @FXML
    private Label rightDateLabel;

    @FXML
    private TextArea leftTextarea;

    @FXML
    private Button nextBtn;

    private List<VTDEntry> entries;
    private Map<String, String> settings;
    private int currentPosition = 0;

    @FXML
    void settingsBtnActionPerformed(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/vtdsettings.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Einstellungen");
            stage.show();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    void backBtnActionPerformed(ActionEvent event) {
        currentPosition -= 2;
        showEntryPage(entries.size() > currentPosition ? entries.get(currentPosition) : null, entries.size() > currentPosition + 1 ? entries.get(currentPosition + 1) : null);
    }

    @FXML
    void nextBtnActionPerformed(ActionEvent event) {
        currentPosition += 2;
        showEntryPage(entries.size() > currentPosition ? entries.get(currentPosition) : null, entries.size() > currentPosition + 1 ? entries.get(currentPosition + 1) : null);
    }

    @FXML
    void closeBtnActionPerformed(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void initialize() {
        loadData();
        injectionCheck();
        showEntryPage(!entries.isEmpty() ? entries.get(0) : null, entries.size() > 0 ? entries.get(1) : null);
    }

    private void loadData() {
        VTDDataAccess dataReader = VTDDataAccess.getInstance();
        entries = dataReader.loadData();
        settings = dataReader.loadSettings();
    }

    private void injectionCheck() {
        assert rightImageview != null : "fx:id=\"rightImageview\" was not injected: check your FXML file 'vtd.fxml'.";
        assert leftDateLabel != null : "fx:id=\"leftDateLabel\" was not injected: check your FXML file 'vtd.fxml'.";
        assert leftImageview != null : "fx:id=\"leftImageview\" was not injected: check your FXML file 'vtd.fxml'.";
        assert rightTextarea != null : "fx:id=\"rightTextarea\" was not injected: check your FXML file 'vtd.fxml'.";
        assert closeBtn != null : "fx:id=\"closeBtn\" was not injected: check your FXML file 'vtd.fxml'.";
        assert backBtn != null : "fx:id=\"backBtn\" was not injected: check your FXML file 'vtd.fxml'.";
        assert rightDateLabel != null : "fx:id=\"rightDateLabel\" was not injected: check your FXML file 'vtd.fxml'.";
        assert leftTextarea != null : "fx:id=\"leftTextarea\" was not injected: check your FXML file 'vtd.fxml'.";
        assert nextBtn != null : "fx:id=\"nextBtn\" was not injected: check your FXML file 'vtd.fxml'.";
    }

    private void checkDisable() {
        if (entries.size() > 0) {
            nextBtn.setDisable(currentPosition == entries.size() - 1 || currentPosition == entries.size() - 2);
            backBtn.setDisable(currentPosition == 0);
        }
    }

    private void showEntryPage(VTDEntry leftEntry, VTDEntry rightEntry) {
        leftImageview.setImage(null);
        leftDateLabel.setText(leftEntry.getHeader().getValue());
        leftTextarea.setText(leftEntry.getText().getValue());
        if (rightEntry != null) {
            rightDateLabel.setText(rightEntry.getHeader().getValue());
            rightTextarea.setText(rightEntry.getText().getValue());
        } else {
            rightDateLabel.setText("");
            rightTextarea.setText("");
            rightImageview.setImage(null);
        }
        loadImage(leftEntry, leftImageview);
        loadImage(rightEntry, rightImageview);
        checkDisable();
        if (settings.get(VTDDataAccess.SETTING_ANIMATIONS) != null && settings.get(VTDDataAccess.SETTING_ANIMATIONS).equals("1")) {
            new MultiImageAnimator(IMAGE_FIT_WIDTH, IMAGE_FIT_HEIGHT, leftImageview, rightImageview).start();
        }
    }

    private void loadImage(VTDEntry entry, ImageView imageView) {
        if (entry != null) {
            try {
                imageView.setFitWidth(IMAGE_FIT_WIDTH);
                imageView.setFitHeight(IMAGE_FIT_HEIGHT);
                String imageString;
                if (entry.getImageUrl().getValueSafe().contains("\\")) {
                    File imageFile = new File(entry.getImageUrl().getValueSafe());
                    imageString = imageFile.toURI().toString();
                } else {
                    imageString = entry.getImageUrl().getValueSafe();
                }
                if (imageString != null && !imageString.isEmpty()) {
                    imageView.setImage(new Image(imageString, IMAGE_CONTAINER_WIDTH, IMAGE_CONTAINER_HEIGHT,
                            true, true));
                } else {
                    imageView.setImage(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showText(String text, TextArea textArea) {
        textArea.setText(text);
    }

    private void increaseSize(ImageView imageView) {
        imageView.setFitHeight(imageView.getFitHeight() + 100);
        imageView.setFitWidth(imageView.getFitWidth() + 100);
    }

    private void decreaseSize(ImageView imageView) {
        imageView.setFitHeight(imageView.getFitHeight() - 100);
        imageView.setFitWidth(imageView.getFitWidth() - 100);
    }
}
