import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.sampled.AudioFormat;
import java.io.File;
import java.net.URL;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        int mono = 1, stereo = 2;
        channelsComboBox.getItems().addAll(mono, stereo, 4);
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
    }

    @FXML
    public void export() {
        var format = collectFromGUI();
        if (format == null) {
            new Alert(Alert.AlertType.ERROR, "Please choose valid values for the audioformat").showAndWait();
            return;
        }
        var filePath = this.fileLocationTextField.getText();
        File f = new File(filePath);
        if (!f.exists()) {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "File already exists. Export anyways?");
            Optional<ButtonType> opt = a.showAndWait();
            if (!(opt.isPresent() && opt.get().equals(ButtonType.OK))) {
                return;
            }
        }
        //collect and export here
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
        boolean bigEndian = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
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
        FileChooser.ExtensionFilter wavFilter = new FileChooser.ExtensionFilter("WAV sound file (*.wav)", "*.wav");
        fileChooser.getExtensionFilters().add(wavFilter);
        File f = fileChooser.showSaveDialog(this.root.getScene().getWindow());
        if (f == null) return;
        this.fileLocationTextField.setText(f.getAbsolutePath());
    }
}
