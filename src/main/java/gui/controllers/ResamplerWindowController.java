package gui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.cell.ComboBoxListCell;
import processing.Resampler;
import utils.SoundClip;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import java.net.URL;
import java.nio.ByteOrder;
import java.util.ResourceBundle;

public class ResamplerWindowController extends EffectWindowController implements Initializable {

    @FXML
    private Label currentFormatLabel;

    @FXML
    private ComboBox<Integer> sampleRateComboBox;

    @FXML
    private ComboBox<Integer> sampleSizeComboBox;

    @FXML
    private ComboBox<Integer> channelsCountComboBox;

    @FXML
    private CheckBox bigEndianCheckBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        channelsCountComboBox.getItems().addAll(1, 2, 4);
        sampleSizeComboBox.getItems().addAll(1, 2, 4);
        sampleRateComboBox.getItems().addAll(8000, 11025, 22050, 32000, 44100, 48000);

        sampleRateComboBox.setCellFactory(c -> new ComboBoxListCell<>(){
            @Override
            public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (!isEmpty()) {
                    float kHz = item / 1000f;
                    setText(String.format("%.2f kHz", kHz));
                } else {
                    setText(null);
                }
                setGraphic(null);
            }
        });

        sampleRateComboBox.setButtonCell(new ComboBoxListCell<>() {
            @Override
            public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (!isEmpty()) {
                    float kHz = item / 1000f;
                    setText(String.format("%.2f kHz", kHz));
                } else {
                    setText(null);
                }
                setGraphic(null);
            }
        });
    }

    @Override
    public void setAudioClip(SoundClip f) {
        super.setAudioClip(f);
        setCurrentFormatDisplay(f.getAudioFormat());
    }

    @Override
    public void setAudioClip(SoundClip f, int start, int end) {
        super.setAudioClip(f, start, end);
        setCurrentFormatDisplay(f.getAudioFormat());
    }

    @FXML
    @Override
    protected void applyEffect() throws Exception {
        AudioFormat fmt = getFromGUI();
        if (fmt == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a valid audio format.").showAndWait();
            return;
        }
        if (!AudioSystem.isConversionSupported(fmt, this.clip.getAudioFormat())) {
            new Alert(Alert.AlertType.WARNING, "Sorry, selected conversion is not supported. Try other combination.").showAndWait();
            return;
        }

        Resampler resampler = new Resampler(this.clip.getAudioFormat(), fmt);
        var newBuffer = resampler.apply(clip.getSamples());
        //hack: use reflection to mutate an immutable object
        var samplesField = clip.getClass().getDeclaredField("samples");
        samplesField.setAccessible(true);
        samplesField.set(clip, newBuffer);
        var formatField = clip.getClass().getDeclaredField("audioFormat");
        formatField.setAccessible(true);
        formatField.set(clip, fmt);
        closeWindow();
    }

    private AudioFormat getFromGUI() {
        Integer sampleRate = null, sampleSize = null, channels = null;
        Boolean isBigEndian = null;
        sampleRate = sampleRateComboBox.getSelectionModel().getSelectedItem();
        sampleSize = sampleSizeComboBox.getSelectionModel().getSelectedItem();
        channels = channelsCountComboBox.getSelectionModel().getSelectedItem();
        isBigEndian = bigEndianCheckBox.isSelected();
        if (sampleRate == null || sampleSize == null || channels == null || isBigEndian == null) {
            return null;
        }
        return new AudioFormat(Float.valueOf(sampleRate), sampleSize * 8, channels, true, isBigEndian);
    }

    private void setCurrentFormatDisplay(AudioFormat fmt) {
        currentFormatLabel.setText(String.format("Current format: \n%s\n", fmt.toString()));
    }
}
