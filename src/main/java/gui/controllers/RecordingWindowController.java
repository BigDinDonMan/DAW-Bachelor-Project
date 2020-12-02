package gui.controllers;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

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

    private java.util.Timer recordingTimer;
    private Stopwatch recordingStopWatch;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        recordingTimer = new java.util.Timer(true);
        recordingStopWatch = Stopwatch.createUnstarted();
    }

    @FXML
    private void startRecording() {
        recordingStopWatch.start();
        recordingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> recordingLengthLabel.setText(
                        DurationFormatUtils.formatDuration(recordingStopWatch.elapsed(TimeUnit.MILLISECONDS), "HH:mm:ss", true))
                );
            }
        }, 0L, 100L);
    }

    @FXML
    public void stopRecording() {
        recordingTimer.cancel();
        if (recordingStopWatch.isRunning()) {
            recordingStopWatch.stop();
            recordingStopWatch.reset();
        }
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
