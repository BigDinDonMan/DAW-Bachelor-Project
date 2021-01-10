package gui.controllers;

import com.google.common.base.Stopwatch;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.time.DurationFormatUtils;
import utils.SoundRecorder;

import javax.sound.sampled.*;
import java.io.File;
import java.net.URL;
import java.nio.ByteOrder;
import java.util.ResourceBundle;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class RecordingWindowController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private Label recordingLengthLabel;
    @FXML
    private ComboBox<Integer> channelsComboBox;
    @FXML
    private ComboBox<Integer> sampleRateComboBox;
    @FXML
    private ComboBox<Integer> sampleSizeComboBox;
    @FXML
    private TextField recordingLocationTextField;
    @FXML
    private Button stopRecordingButton;
    @FXML
    private Button startRecordingButton;

    private java.util.Timer recordingTimer;
    private Stopwatch recordingStopWatch;
    private SoundRecorder recorder;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        recordingTimer = new java.util.Timer(true);
        recordingStopWatch = Stopwatch.createUnstarted();
        startRecordingButton.setDisable(false);
        stopRecordingButton.setDisable(true);
        channelsComboBox.getItems().addAll(1, 2);
        sampleRateComboBox.getItems().addAll(8000, 16000);
        sampleSizeComboBox.getItems().addAll(1, 2, 4); //bytes
    }

    private AudioFormat getFromGui() {
        Integer channels, sampleRate, sampleSize;
        channels = channelsComboBox.getSelectionModel().getSelectedItem();
        sampleRate = sampleRateComboBox.getSelectionModel().getSelectedItem();
        sampleSize = sampleSizeComboBox.getSelectionModel().getSelectedItem();
        if (channels == null || sampleRate == null || sampleSize == null) return null;
        return new AudioFormat(
                sampleRate,
                sampleSize * 8,
                channels,
                true,
                ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN
        );
    }

    @FXML
    private void startRecording() {
        recordingLengthLabel.setText("00:00:00");
        File outputFile = new File(recordingLocationTextField.getText());
        AudioFormat recordingFormat = getFromGui();
        if (recordingFormat == null) {
            new Alert(Alert.AlertType.ERROR, "Please select valid format").showAndWait();
            return;
        }
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, recordingFormat);
        if (!AudioSystem.isLineSupported(info)) {
            new Alert(Alert.AlertType.ERROR, String.format("Sorry, format %s is unsupported", recordingFormat.toString())).showAndWait();
            return;
        }
        try {
            recorder = new SoundRecorder(recordingFormat, (TargetDataLine)AudioSystem.getLine(info), outputFile);
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
        FileChooser.ExtensionFilter wavFilter = new FileChooser.ExtensionFilter("WAV sound clip (*.wav)", "*.wav");
        fileChooser.getExtensionFilters().add(wavFilter);
        var chosenFile = fileChooser.showSaveDialog(this.root.getScene().getWindow());
        if (chosenFile != null) {
            recordingLocationTextField.setText(chosenFile.getAbsolutePath());
        }
    }
}
