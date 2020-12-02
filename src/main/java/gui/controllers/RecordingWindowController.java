package gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class RecordingWindowController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private ComboBox<Integer> sampleRateComboBox;
    @FXML
    private ComboBox<Integer> channelsComboBox;
    @FXML
    private Label recordingLengthLabel;
    @FXML
    private TextField recordingLocationTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void startRecording() {

    }

    @FXML
    private void stopRecording() {

    }

    @FXML
    private void chooseRecordingLocation() {
        var fileChooser = new FileChooser();
        var chosenFile = fileChooser.showSaveDialog(this.root.getScene().getWindow());
        if (chosenFile != null) {
            recordingLocationTextField.setText(chosenFile.getAbsolutePath());
        }
    }
}
