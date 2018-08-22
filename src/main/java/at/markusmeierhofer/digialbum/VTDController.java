package at.markusmeierhofer.digialbum;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VTDController {
    private static final int IMAGE_CONTAINER_WIDTH = 1024;
    private static final int IMAGE_CONTAINER_HEIGHT = 768;
    private static final int IMAGE_FIT_WIDTH = 480;
    private static final int IMAGE_FIT_HEIGHT = 425;

    // For previews
    private static final int PREVIEW_FIT_WIDTH = 100;
    private static final int PREVIEW_WIDTH = 100;
    private static final int PREVIEW_HEIGHT = 200;

    @FXML
    private Label headerLbl;

    @FXML
    private ImageView rightImageview;

    @FXML
    private ImageView leftImageview;

    @FXML
    private Label leftDateLabel;

    @FXML
    private TextArea rightTextarea;

    @FXML
    private ListView<VTDEntry> imagePreviewList;

    @FXML
    private Button closeBtn;

    @FXML
    private Button backBtn;

    @FXML
    private Label rightDateLabel;

    @FXML
    private Button settingsBtn;

    @FXML
    private TextArea leftTextarea;

    @FXML
    private Button nextBtn;

    private static final Logger LOGGER = LogManager.getLogger();

    private List<VTDEntry> entries;
    private int currentPosition;
    private VTDConfig config;

    @FXML
    void settingsBtnActionPerformed(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/vtdsettings.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Einstellungen");
            stage.show();
        } catch (IOException ex) {
            LOGGER.catching(ex);
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
        config = VTDConfig.getInstance();
        headerLbl.setText(config.getHeaderText());
        currentPosition = 0;
        loadData();
        injectionCheck();
        initializeListView();
        showEntryPage(!entries.isEmpty() ? entries.get(0) : null, entries.size() > 0 ? entries.get(1) : null);
    }

    private void initializeListView() {
        Map<VTDEntry, ImageView> preloadedImages = getPreloadedImages(entries);

        imagePreviewList.setItems(FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(entries)));
        imagePreviewList.setCellFactory((ListView<VTDEntry> param) -> new ListCell<VTDEntry>() {
            @Override
            protected void updateItem(VTDEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(preloadedImages.get(item));
                    setText(item.getHeader().getValueSafe());
                }
            }
        });
        imagePreviewList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        imagePreviewList.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends VTDEntry> observable, VTDEntry oldValue, VTDEntry newValue) ->
                        showEntryPageFromPreviewEntry(newValue));
    }

    private void showEntryPageFromPreviewEntry(VTDEntry selectedPreviewEntry) {
        if (entries.indexOf(selectedPreviewEntry) == -1) {
            return;
        }
        int index = entries.indexOf(selectedPreviewEntry);
        if (entries.indexOf(selectedPreviewEntry) % 2 == 0) { // If it's an even index - show the image and its next image
            currentPosition = index;
            VTDEntry nextEntry = entries.size() > (index + 1) ? entries.get(index + 1) : null;
            showEntryPage(selectedPreviewEntry, nextEntry);
        } else { // it's an uneven index - show the image and its previous image
            currentPosition = index - 1;
            showEntryPage(entries.get(index - 1), selectedPreviewEntry);
        }
    }

    private Map<VTDEntry, ImageView> getPreloadedImages(List<VTDEntry> entries) {
        return entries.stream()
                .map(entry -> {
                    ImageView previewImageView = new ImageView();
                    loadPreviewImage(entry, previewImageView);
                    return new AbstractMap.SimpleEntry<>(entry, previewImageView);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void loadData() {
        VTDDataAccess dataReader = VTDDataAccess.getInstance();
        entries = dataReader.loadData();
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
        loadFullSizeImage(leftEntry, leftImageview);
        loadFullSizeImage(rightEntry, rightImageview);
        checkDisable();
        if (config.isUseAnimations()) {
            new MultiImageAnimator(IMAGE_FIT_WIDTH, IMAGE_FIT_HEIGHT, leftImageview, rightImageview).start();
        }
    }

    private ImageView loadPreviewImage(VTDEntry entry, ImageView imageView) {
        loadImage(entry, imageView, PREVIEW_FIT_WIDTH, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT, false,
                true);
        return imageView;
    }

    private void loadFullSizeImage(VTDEntry entry, ImageView imageView) {
        loadImage(entry, imageView, IMAGE_FIT_WIDTH, IMAGE_FIT_HEIGHT, IMAGE_CONTAINER_WIDTH, IMAGE_CONTAINER_HEIGHT,
                true, false);
    }

    private void loadImage(VTDEntry entry, ImageView imageView, double fitWidth, double fitHeight, double requestedWidth,
                           double requestedHeight, boolean smooth, boolean backgroundLoading) {
        if (entry != null) {
            try {
                if (fitWidth > 0) {
                    imageView.setFitWidth(fitWidth);
                }
                if (fitHeight > 0) {
                    imageView.setFitHeight(fitHeight);
                }
                String imageString = entry.getImageUrl().getValueSafe();
                if (!entry.getImageUrl().getValueSafe().contains(":\\")) {
                    imageString = config.getBasePath() + imageString;
                }
                if (!imageString.isEmpty()) {
                    imageView.setImage(new Image(new File(imageString).toURI().toString(),
                            requestedWidth, requestedHeight,
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
