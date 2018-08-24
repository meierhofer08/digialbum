package at.markusmeierhofer.digialbum.gui.mainpage;

import at.markusmeierhofer.digialbum.VTDEntry;
import at.markusmeierhofer.digialbum.bl.config.settings.VTDSettings;
import at.markusmeierhofer.digialbum.dl.DataFileNotFoundException;
import at.markusmeierhofer.digialbum.dl.VTDDataAccess;
import at.markusmeierhofer.digialbum.gui.helpers.MultiImageAnimator;
import at.markusmeierhofer.digialbum.gui.settings.VTDSettingsController;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    // Text strings
    private static final String NO_MORE_PICTURE_PRESENT = "Kein weiteres Bild vorhanden!";
    private static final String PICTURE_NOT_FOUND = "Bild nicht gefunden!";
    private static final String DATAFILE_NOT_FOUND_HEADER = "VTDData nicht gefunden";
    private static final String DATAFILE_NOT_FOUND = "Die Datei VTDData.dat wurde nicht im Basispfad %s gefunden!\n" +
            "Bitte setzen sie den korrekten Basispfad oder erstellen Sie die Datei durch hinzufügen von Bildern " +
            "in den Einstellungen.";

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

    @FXML
    private Label leftPlaceholder;

    @FXML
    private Label rightPlaceholder;

    private static final Logger LOGGER = LogManager.getLogger();

    private List<VTDEntry> entries;
    private int currentPosition;
    private VTDSettings settings;
    private VTDEntry currentLeftEntry;
    private VTDEntry currentRightEntry;
    private ExecutorService animationExecutor;
    private MultiImageAnimator currentMultiImageAnimator;

    @FXML
    void settingsBtnActionPerformed(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vtdsettings.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Einstellungen");
            stage.showAndWait();
            VTDSettingsController settingsController = loader.getController();
            if (settingsController.areSettingsSaved()) {
                LOGGER.info("ReInit Main View");
                reInit();
            } else {
                LOGGER.info("Don't reInit Main View");
            }
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
        injectionCheck();
        firstInit();
        reInit();
    }

    private void reInit() {
        currentPosition = 0;
        currentLeftEntry = null;
        currentRightEntry = null;
        loadData();
        initializeImagePreviews();
        showEntryPage(!entries.isEmpty() ? entries.get(0) : null, entries.size() > 1 ? entries.get(1) : null);
    }

    private void firstInit() {
        settings = VTDSettings.getInstance();
        headerLbl.setText(settings.getHeaderText());
        animationExecutor = Executors.newSingleThreadExecutor();
        imagePreviewList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        imagePreviewList.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends VTDEntry> observable, VTDEntry oldValue, VTDEntry newValue) ->
                {
                    if (!Objects.equals(currentLeftEntry, newValue) && !Objects.equals(currentRightEntry, newValue)) {
                        showEntryPageFromPreviewEntry(newValue);
                    }
                });
    }

    private void initializeImagePreviews() {
        Map<VTDEntry, Optional<ImageView>> preloadedImages = getPreloadedImages(entries);

        imagePreviewList.scrollTo(0);
        imagePreviewList.setItems(FXCollections.observableArrayList(entries));
        imagePreviewList.setCellFactory((ListView<VTDEntry> param) -> new ListCell<VTDEntry>() {
            @Override
            protected void updateItem(VTDEntry entry, boolean empty) {
                super.updateItem(entry, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Optional<ImageView> previewImage = preloadedImages.get(entry);
                    if (previewImage != null && previewImage.isPresent()) {
                        setGraphic(previewImage.get());
                        setText(entry.getHeader().getValueSafe());
                    } else {
                        setGraphic(null);
                        setText(getPictureNotFoundText(entry));
                    }
                }
            }
        });
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

    private Map<VTDEntry, Optional<ImageView>> getPreloadedImages(List<VTDEntry> entries) {
        Label dummyPlaceholder = new Label();
        return entries.stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry,
                        loadPreviewImage(entry, new ImageView(), dummyPlaceholder)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void loadData() {
        VTDDataAccess dataReader = VTDDataAccess.getInstance();
        try {
            entries = dataReader.loadData();
        } catch (DataFileNotFoundException e) {
            entries = new ArrayList<>();
            showDataFileNotFoundAlert();
        }
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
        } else {
            nextBtn.setDisable(true);
            backBtn.setDisable(true);
        }
    }

    private void showEntryPage(VTDEntry leftEntry, VTDEntry rightEntry) {
        currentLeftEntry = leftEntry;
        currentRightEntry = rightEntry;

        // dirty fix for the pictures sometimes freezing when animated
        leftImageview.setVisible(false);
        rightImageview.setVisible(false);

        if (leftEntry != null) {
            leftImageview.setImage(null);
            leftDateLabel.setText(leftEntry.getHeader().getValue());
            leftTextarea.setText(leftEntry.getText().getValue());
        } else {
            leftDateLabel.setText("");
            leftTextarea.setText("");
            leftImageview.setImage(null);
        }
        if (rightEntry != null) {
            rightDateLabel.setText(rightEntry.getHeader().getValue());
            rightTextarea.setText(rightEntry.getText().getValue());
        } else {
            rightDateLabel.setText("");
            rightTextarea.setText("");
            rightImageview.setImage(null);
        }

        double fitWidth;
        double fitHeight;
        if (settings.isUseAnimations()) {
            if (currentMultiImageAnimator != null) {
                currentMultiImageAnimator.stop();
            }
            currentMultiImageAnimator = new MultiImageAnimator(IMAGE_FIT_WIDTH, IMAGE_FIT_HEIGHT, leftImageview, rightImageview);
            fitWidth = currentMultiImageAnimator.getStartWidth();
            fitHeight = currentMultiImageAnimator.getStartHeight();
        } else {
            fitWidth = IMAGE_FIT_WIDTH;
            fitHeight = IMAGE_FIT_HEIGHT;
        }
        loadFullSizeImage(leftEntry, leftImageview, leftPlaceholder, fitWidth, fitHeight);
        loadFullSizeImage(rightEntry, rightImageview, rightPlaceholder, fitWidth, fitHeight);
        if (settings.isUseAnimations()) {
            animationExecutor.execute(currentMultiImageAnimator);
        }
        checkDisable();
    }

    private Optional<ImageView> loadPreviewImage(VTDEntry entry, ImageView imageView, Label placeholder) {
        boolean loaded = loadImage(entry, imageView, placeholder, PREVIEW_FIT_WIDTH, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT, false,
                true);
        if (loaded) {
            return Optional.of(imageView);
        } else {
            return Optional.empty();
        }
    }

    private void loadFullSizeImage(VTDEntry entry, ImageView imageView, Label placeholder, double fitWidth, double fitHeight) {
        loadImage(entry, imageView, placeholder, fitWidth, fitHeight, IMAGE_CONTAINER_WIDTH, IMAGE_CONTAINER_HEIGHT,
                true, false);
    }

    private boolean loadImage(VTDEntry entry, ImageView imageView, Label placeholder, double fitWidth, double fitHeight, double requestedWidth,
                              double requestedHeight, boolean smooth, boolean backgroundLoading) {
        boolean loaded = false;
        if (entry != null) {
            try {
                if (fitWidth > 0) {
                    imageView.setFitWidth(fitWidth);
                }
                if (fitHeight > 0) {
                    imageView.setFitHeight(fitHeight);
                }
                String imageString = getResolvedImageString(entry.getImageUrl().getValueSafe());
                if (!imageString.isEmpty()) {
                    File imageFile = new File(imageString);
                    if (imageFile.exists()) {
                        imageView.setImage(new Image(imageFile.toURI().toString(),
                                requestedWidth, requestedHeight,
                                true, smooth, backgroundLoading));
                        loaded = true;
                    }
                } else {
                    imageView.setImage(null);
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        } else {
            imageView.setImage(null);
        }

        imageView.setVisible(loaded);
        placeholder.setVisible(!loaded);
        if (!loaded) {
            placeholder.setText(getPictureNotFoundText(entry));
        }

        return loaded;
    }

    private String getResolvedImageString(String imageString) {
        String resolvedImageString = imageString;
        if (!resolvedImageString.contains(":\\")) {
            resolvedImageString = settings.getBasePath() + resolvedImageString;
        }

        return resolvedImageString;
    }

    private String getPictureNotFoundText(VTDEntry entry) {
        if (entry == null) {
            return NO_MORE_PICTURE_PRESENT;
        } else {
            return PICTURE_NOT_FOUND + " (" + getResolvedImageString(entry.getImageUrl().getValueSafe()) + ")";
        }
    }

    private void showDataFileNotFoundAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(DATAFILE_NOT_FOUND_HEADER);
        alert.setHeaderText(null);
        alert.setContentText(String.format(DATAFILE_NOT_FOUND, settings.getBasePath()));

        alert.showAndWait();
    }

    public void shutdown() {
        animationExecutor.shutdown();
    }
}
