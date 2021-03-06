package at.markusmeierhofer.digialbum.gui.settings;

import at.markusmeierhofer.digialbum.VTDEntry;
import at.markusmeierhofer.digialbum.bl.config.StringKey;
import at.markusmeierhofer.digialbum.bl.config.VTDConfig;
import at.markusmeierhofer.digialbum.dl.DataFileNotFoundException;
import at.markusmeierhofer.digialbum.dl.VTDDataAccess;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ManagePicturesController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label headerLbl;

    @FXML
    private GridPane entryGridPane;

    @FXML
    private ListView<VTDEntry> entryListView;

    @FXML
    private Label descriptionLbl;

    @FXML
    private Button downBtn;

    @FXML
    private Button upBtn;

    @FXML
    private TextField imageUrlTF;

    @FXML
    private Button chooseImageBtn;

    @FXML
    private Button removeBtn;

    @FXML
    private Label imageUrlLabel;

    @FXML
    private TextField headerTF;

    @FXML
    private TextArea descriptionTA;

    @FXML
    private Button addBtn;

    @FXML
    private Button saveBtn;

    private ObservableList<VTDEntry> entries = FXCollections.observableArrayList();
    private VTDDataAccess dataAccess;
    private VTDConfig config;
    private boolean settingsSaved;
    private File lastDirectory;

    @FXML
    void addBtnActionPerformed(ActionEvent event) {
        entries.add(new VTDEntry());
    }

    @FXML
    void removeBtnActionPerformed(ActionEvent event) {
        entries.remove(entryListView.getSelectionModel().getSelectedIndex());
    }

    @FXML
    void upBtnActionPerformed(ActionEvent event) {
        VTDEntry selectedEntry = entryListView.getSelectionModel().getSelectedItem();
        int selectedIndex = entryListView.getSelectionModel().getSelectedIndex();
        entries.remove(entryListView.getSelectionModel().getSelectedIndex());
        entries.add(selectedIndex - 1, selectedEntry);
        entryListView.getSelectionModel().select(selectedIndex - 1);
    }

    @FXML
    void downBtnActionPerformed(ActionEvent event) {
        VTDEntry selectedEntry = entryListView.getSelectionModel().getSelectedItem();
        int selectedIndex = entryListView.getSelectionModel().getSelectedIndex();
        entries.remove(entryListView.getSelectionModel().getSelectedIndex());
        entries.add(selectedIndex + 1, selectedEntry);
        entryListView.getSelectionModel().select(selectedIndex + 1);
    }

    @FXML
    void chooseImageBtnActionPerformed(ActionEvent event) {
        chooseImage(getStage(event));
    }

    private void chooseImage(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        if (lastDirectory != null) {
            fileChooser.setInitialDirectory(lastDirectory);
        }
        fileChooser.setTitle("Bitte Bild auswählen...");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Alle Bildformate",
                        "*.jpg", "*.png", "*.tiff", "*.gif", "*.jpeg"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            lastDirectory = file.getParentFile();
            imageUrlTF.setText(file.getAbsolutePath());
            VTDEntry selectedEntry = entryListView.getSelectionModel().getSelectedItem();
            if (selectedEntry != null) {
                if (file.getParentFile().equals(new File(config.getSettings().getBasePath()))) {
                    selectedEntry.setImageUrl(file.getName());
                } else {
                    selectedEntry.setImageUrl(file.getAbsolutePath());
                }
            }
        }
    }

    @FXML
    void saveBtnActionPerformed(ActionEvent event) {
        settingsSaved = true;
        dataAccess.saveData(entries);
        close(event);
    }

    private Stage getStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        return (Stage) source.getScene().getWindow();
    }

    private void close(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    void initialize() {
        injectionCheck();
        firstInit();
        reInit();
    }

    private void firstInit() {
        loadButtonIcons();
        dataAccess = VTDDataAccess.getInstance();
        config = VTDConfig.getInstance();
        initializeTexts();
    }

    private void initializeTexts() {
        chooseImageBtn.setText(config.getString(StringKey.DATA_PAGE_CHOOSE_IMAGE_BTN));
        saveBtn.setText(config.getString(StringKey.DATA_PAGE_SAVE_BTN));
        headerLbl.setText(config.getString(StringKey.DATA_PAGE_HEADER_LBL));
        imageUrlLabel.setText(config.getString(StringKey.DATA_PAGE_IMAGE_URL_LBL));
        descriptionLbl.setText(config.getString(StringKey.DATA_PAGE_DESCRIPTION_LBL));
    }

    private void reInit() {
        try {
            entries = FXCollections.observableArrayList(dataAccess.loadData());
        } catch (DataFileNotFoundException e) {
            entries = FXCollections.observableArrayList();
        }
        removeBtn.setDisable(true);
        upBtn.setDisable(true);
        downBtn.setDisable(true);
        entryGridPane.setDisable(true);
        initEntryListView();
        settingsSaved = false;
    }

    public boolean areSettingsSaved() {
        return settingsSaved;
    }

    private void initEntryListView() {
        entryListView.setItems(entries);
        entryListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        entryListView.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue<? extends VTDEntry> observable, VTDEntry oldValue, VTDEntry newValue) -> {
                    if (oldValue != null) {
                        headerTF.textProperty().unbindBidirectional(oldValue.getHeader());
                        imageUrlTF.textProperty().unbindBidirectional(oldValue.getImageUrl());
                        descriptionTA.textProperty().unbindBidirectional(oldValue.getText());
                    }
                    if (newValue == null) {
                        headerTF.setText("");
                        imageUrlTF.setText("");
                        descriptionTA.setText("");
                        entryGridPane.setDisable(true);
                        removeBtn.setDisable(true);
                        upBtn.setDisable(true);
                        downBtn.setDisable(true);
                    } else {
                        headerTF.textProperty().bindBidirectional(newValue.getHeader());
                        imageUrlTF.textProperty().bindBidirectional(newValue.getImageUrl());
                        descriptionTA.textProperty().bindBidirectional(newValue.getText());
                        entryGridPane.setDisable(false);
                        removeBtn.setDisable(false);

                        if (entries != null && !entries.isEmpty()) {
                            upBtn.setDisable(newValue.equals(entries.get(0)));
                            downBtn.setDisable(newValue.equals(entries.get(entries.size() - 1)));
                        }
                    }
                });
    }

    private void loadButtonIcons() {
        addBtn.setGraphic(new ImageView(new Image("Alarm-Plus-icon.png")));
        removeBtn.setGraphic(new ImageView(new Image("Alarm-Private-icon.png")));
        upBtn.setGraphic(new ImageView(new Image("Alarm-Arrow-Up-icon.png")));
        downBtn.setGraphic(new ImageView(new Image("Alarm-Arrow-Down-icon.png")));
    }

    private void injectionCheck() {
        assert headerLbl != null : "fx:id=\"headerLbl\" was not injected: check your FXML file 'managePictures.fxml'.";
        assert entryListView != null : "fx:id=\"entryListView\" was not injected: check your FXML file 'managePictures.fxml'.";
        assert descriptionLbl != null : "fx:id=\"descriptionLbl\" was not injected: check your FXML file 'managePictures.fxml'.";
        assert downBtn != null : "fx:id=\"downBtn\" was not injected: check your FXML file 'managePictures.fxml'.";
        assert upBtn != null : "fx:id=\"upBtn\" was not injected: check your FXML file 'managePictures.fxml'.";
        assert imageUrlTF != null : "fx:id=\"imageUrlTF\" was not injected: check your FXML file 'managePictures.fxml'.";
        assert removeBtn != null : "fx:id=\"removeBtn\" was not injected: check your FXML file 'managePictures.fxml'.";
        assert imageUrlLabel != null : "fx:id=\"imageUrlLabel\" was not injected: check your FXML file 'managePictures.fxml'.";
        assert headerTF != null : "fx:id=\"headerTF\" was not injected: check your FXML file 'managePictures.fxml'.";
        assert descriptionTA != null : "fx:id=\"descriptionTA\" was not injected: check your FXML file 'managePictures.fxml'.";
        assert addBtn != null : "fx:id=\"addBtn\" was not injected: check your FXML file 'managePictures.fxml'.";
        assert saveBtn != null : "fx:id=\"saveBtn\" was not injected: check your FXML file 'managePictures.fxml'.";
    }
}
