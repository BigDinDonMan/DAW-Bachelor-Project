package gui.controllers;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.time.DurationFormatUtils;
import utils.SoundRecorder;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteOrder;
import java.util.ResourceBundle;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RecordingWindowController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private Label recordingLengthLabel;
    @FXML
    private TextField recordingLocationTextField;
    @FXML
    private Button stopRecordingButton;
    @FXML
    private Button startRecordingButton;

    private AudioFormat recordingFormat;
    private java.util.Timer recordingTimer;
    private Stopwatch recordingStopWatch;
    private SoundRecorder recorder;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        recordingTimer = new java.util.Timer(true);
        recordingStopWatch = Stopwatch.createUnstarted();
        recordingFormat = new AudioFormat(8000, 16, 2,
                true, ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN);
        startRecordingButton.setDisable(false);
        stopRecordingButton.setDisable(true);
    }

    @FXML
    private void startRecording() {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, recordingFormat);
        File outputFile = new File(recordingLocationTextField.getText());
        try {
            recorder = new SoundRecorder((TargetDataLine)AudioSystem.getLine(info), outputFile);
            Thread recordingThread = new Thread(recorder);
            recordingThread.start();
            recordingStopWatch.start();
            recordingTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> recordingLengthLabel.setText(
                            DurationFormatUtils.formatDuration(recordingStopWatch.elapsed(TimeUnit.MILLISECONDS), "HH:mm:ss", true))
                    );
                }
            }, 0L, 100L);
            startRecordingButton.setDisable(true);
            stopRecordingButton.setDisable(false);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void stopRecording() {
        if (recorder != null) {
            recorder.stop();
        }
        recordingTimer.cancel();
        if (recordingStopWatch.isRunning()) {
            recordingStopWatch.stop();
            recordingStopWatch.reset();
        }
        startRecordingButton.setDisable(false);
        stopRecordingButton.setDisable(true);
    }

    @FXML
    private void chooseRecordingLocation() {
        var fileChooser = new FileChooser();
        FileChooser.ExtensionFilter wavFilter = new FileChooser.ExtensionFilter("WAV sound file (*.wav)", "*.wav");
        fileChooser.getExtensionFilters().add(wavFilter);
        var chosenFile = fileChooser.showSaveDialog(this.root.getScene().getWindow());
        if (chosenFile != null) {
            recordingLocationTextField.setText(chosenFile.getAbsolutePath());
        }
    }
}
