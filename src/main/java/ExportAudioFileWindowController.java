import gui.controls.WaveformViewersContainer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.SoundFileExporter;
import utils.TimelineToClipMapper;

import javax.sound.sampled.AudioFormat;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteOrder;
import java.util.*;

public class ExportAudioFileWindowController implements Initializable {

    @FXML
    private ComboBox<Integer> channelsComboBox;
    @FXML
    private ComboBox<Integer> sampleRateComboBox;
    @FXML
    private ComboBox<Integer> sampleSizeComboBox;
    @FXML
    private TextField fileLocationTextField;

    @FXML
    private AnchorPane root;

    private List<WaveformViewersContainer> containers;

    public ExportAudioFileWindowController(Collection<WaveformViewersContainer> containers) {
        this.containers = new ArrayList<>(containers);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        int mono = 1, stereo = 2;
        channelsComboBox.getItems().addAll(mono, stereo, 4);
        channelsComboBox.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(null);
                if (!isEmpty()) {
                    switch (item) {
                        case 1:
                            setText("Mono");
                            break;
                        case 2:
                            setText("Stereo");
                            break;
                        default:
                            setText(String.format("%d channels", item));
                    }
                } else {
                    setText(null);
                }
            }
        });
        channelsComboBox.setCellFactory(callback -> new ListCell<>(){
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(null);
                if (!isEmpty()) {
                    switch (item) {
                        case 1:
                            setText("Mono");
                            break;
                        case 2:
                            setText("Stereo");
                            break;
                        default:
                            setText(String.format("%d channels", item));
                    }
                } else {
                    setText(null);
                }
            }
        });

        sampleRateComboBox.getItems().addAll(8000, 11025, 22050, 32000, 44100, 48000);
        sampleRateComboBox.setCellFactory(callback -> new ListCell<>(){
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(null);
                if (!isEmpty()) {
                    float kHz = item / 1000f;
                    setText(String.format("%.2f kHz", kHz));
                } else {
                    setText(null);
                }
            }
        });
        sampleRateComboBox.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(null);
                if (!isEmpty()) {
                    float kHz = item / 1000f;
                    setText(String.format("%.2f kHz", kHz));
                } else {
                    setText(null);
                }
            }
        });

        sampleSizeComboBox.getItems().addAll(1, 2, 4);
        sampleSizeComboBox.setCellFactory(callback -> new ListCell<>(){
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(null);
                if (!isEmpty()) {
                    switch (item) {
                        case 1:
                            setText("8-bit signed integer");
                            break;
                        case 2:
                            setText("16-bit signed integer");
                            break;
                        case 4:
                            setText("32-bit float (range <-1, 1>)");
                            break;
                    }
                }
            }
        });
        sampleSizeComboBox.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(null);
                if (!isEmpty()) {
                    switch (item) {
                        case 1:
                            setText("8-bit signed integer");
                            break;
                        case 2:
                            setText("16-bit signed integer");
                            break;
                        case 4:
                            setText("32-bit float (range <-1, 1>)");
                            break;
                    }
                }
            }
        });

        fileLocationTextField.setEditable(false);
    }

    @FXML
    public void export() {
        var format = collectFromGUI();
        if (format == null) {
            new Alert(Alert.AlertType.ERROR, "Please choose valid values for the audioformat").showAndWait();
            return;
        }
        var filePath = this.fileLocationTextField.getText();
        TimelineToClipMapper mapper = new TimelineToClipMapper(format, containers);

        var result = mapper.map();
        //collect and export here
        SoundFileExporter fe = new SoundFileExporter(result);
        try {
            fe.export(filePath);
            new Alert(Alert.AlertType.INFORMATION, "Exported successfully to " + filePath).showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    @FXML
    private void close() {
        ((Stage)this.root.getScene().getWindow()).close();
    }

    private AudioFormat collectFromGUI() {
        Integer channels, sampleSize, sampleRate;
        channels = channelsComboBox.getSelectionModel().getSelectedItem();
        sampleSize = sampleSizeComboBox.getSelectionModel().getSelectedItem();
        sampleRate = sampleRateComboBox.getSelectionModel().getSelectedItem();
        if (channels == null || sampleSize == null || sampleRate == null) return null;
        //frame size = sample size * channels
        int frameSize = sampleSize * channels;
        var encoding = AudioFormat.Encoding.PCM_SIGNED;
        boolean bigEndian = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);
        return new AudioFormat(
                encoding,
                sampleRate,
                sampleSize * 8,
                channels,
                frameSize,
                sampleRate,
                bigEndian
        );
    }

    @FXML
    private void chooseFileSaveLocation() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter wavFilter = new FileChooser.ExtensionFilter("WAV sound clip (*.wav)", "*.wav");
        fileChooser.getExtensionFilters().add(wavFilter);
        File f = fileChooser.showSaveDialog(this.root.getScene().getWindow());
        if (f == null) return;
        this.fileLocationTextField.setText(f.getAbsolutePath());
    }
}
