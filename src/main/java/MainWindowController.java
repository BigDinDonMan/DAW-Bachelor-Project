import gui.controls.WaveformViewer;
import gui.controls.WaveformViewersContainer;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import utils.ArrayUtils;
import utils.AudioFile;
import utils.AudioPlayer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainWindowController implements Initializable {

    private Stage mainStage;

    @FXML
    private ScrollPane waveformViewersContainer;
    @FXML
    private VBox waveformsVBox;

    private List<WaveformViewersContainer> containers;


    public MainWindowController() {
        containers = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        waveformsVBox.getChildren().addListener((ListChangeListener<Node>) change -> {
            while (change.next()) { // this has to be here, otherwise listener throws IllegalStateException
                if (change.wasAdded()) {
                    var added = change.getAddedSubList();
                    containers.addAll(added.stream().map(n -> (WaveformViewersContainer)n).collect(Collectors.toList()));
                } else if (change.wasRemoved()) {
                    var removed = change.getRemoved();
                    containers.removeAll(removed.stream().map(n -> (WaveformViewersContainer)n).collect(Collectors.toList()));
                }
            }
        });
        try {
            AudioFile file = new AudioFile(System.getProperty("user.dir") + File.separator + "00_otusznje.wav");
            WaveformViewersContainer container = new WaveformViewersContainer();
            containers.add(container);
            WaveformViewer viewer = new WaveformViewer(file);
            waveformsVBox.getChildren().add(container);
            container.addWaveForm(viewer);
        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clearWaveformViewersSelection() {
        WaveformViewer.clearSelections();
    }

    @FXML
    private void addNewWaveformContainer() {
        var container = new WaveformViewersContainer();
        container.setMinWidth(1000d);
        container.setPrefWidth(1000d);
        waveformsVBox.getChildren().add(container);
    }

    @FXML
    private void deleteLastWaveformContainer() {
        var containerToDelete = containers.get(containers.size() - 1);
        if (containerToDelete.getChildren().size() > 0) {
            var decision = new Alert(Alert.AlertType.CONFIRMATION, "Really delete it? There are tracks on that track line").showAndWait();
            if (decision.isPresent() && decision.get().equals(ButtonType.OK)) {
                containers.remove(containerToDelete);
                waveformsVBox.getChildren().remove(containerToDelete);
            }
        } else {
            containers.remove(containerToDelete);
            waveformsVBox.getChildren().remove(containerToDelete);
        }
    }

    @FXML
    private void extendContainersSize() {
        containers.forEach(c -> {
            var newWidth = c.getWidth() + 1000d;
            c.setMinWidth(newWidth);
            c.setPrefWidth(newWidth);
        });
    }

    private AudioPlayer audioPlayer;


    @FXML
    private void playTimeline() {
        if (audioPlayer == null) {
            audioPlayer = new AudioPlayer();
        }
        //if selection is present, play just the selection
        //else play everything
        WaveformViewer selected = WaveformViewer.getSelected();
        if (selected != null) {
            if (selected.getAudioFile() != audioPlayer.getAudioFile()) {
                audioPlayer.setAudioFile(selected.getAudioFile());
            }
            audioPlayer.play();
        } else {

        }
    }

    @FXML
    private void pauseTimeline() {
        if (audioPlayer.isPlaying()) {
            audioPlayer.pause();
        }
    }

    @FXML
    private void stopTimeline() {
        if (audioPlayer.isPlaying()) {
            audioPlayer.pause();
            audioPlayer.reset();
        }
    }

    @FXML
    private void exportSoundFile() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File chosenDirectory = directoryChooser.showDialog(this.mainStage);
        if (chosenDirectory == null) return;
    }

    @FXML
    private void cutAudioFragment() {
        var selected = WaveformViewer.getSelected();
        if (selected == null) {
            new Alert(Alert.AlertType.ERROR, "Cannot cut non-selected audio fragment! Please select it first").showAndWait();
            return;
        }
        var opt = selected.getSelectionBufferBounds();
        opt.ifPresentOrElse(bounds -> {
            int start = bounds.getValue0();
            int end = bounds.getValue1();
            int samplesPerPixel = selected.getSamplesPerPixel();
            int bufferStart = ensureAudioFrameSize(
                    samplesPerPixel * start,
                    selected.getAudioFile().getAudioFormat(),
                    true
            );
            int bufferEnd = ensureAudioFrameSize(
                    samplesPerPixel * end,
                    selected.getAudioFile().getAudioFormat(),
                    false
            );
            AudioFile file = selected.getAudioFile();
            float[] samples = file.getSamples();
            if (start == 0 && end == (int)selected.getWidth()) return;
            if (start == 0 || end == (int)selected.getWidth()) {//split viewer into 2 from the start
                float[] first = ArrayUtils.slice(samples, bufferStart, bufferEnd);
                float[] second = ArrayUtils.slice(samples, bufferEnd, samples.length);
                AudioFile f1, f2;
                AudioFormat fmt = file.getAudioFormat();
                f1 = new AudioFile(file.getAudioFormat(), first);
                f2 = new AudioFile(AudioFile.copyFormat(fmt), second);

                WaveformViewersContainer container = ((WaveformViewersContainer)selected.getParent());

                container.getChildren().remove(selected);
                WaveformViewer.remove(selected);

                WaveformViewer v1, v2;
                v1 = new WaveformViewer(f1);
                v2 = new WaveformViewer(f2);

                container.addWaveForm(v1);
                container.addWaveForm(v2);
                v2.setLayoutX(v1.getPrefWidth());
                extendContainersSize();

            } else { //cut it into 3 viewers
                //todo: fix adding the items, if you cut the sound at the end it appends as if container was empty
                float[] starting = ArrayUtils.slice(samples, 0, bufferStart);
                float[] middle = ArrayUtils.slice(samples, bufferStart, bufferEnd);
                float[] ending = ArrayUtils.slice(samples, bufferEnd, samples.length);
                AudioFile f1, f2, f3;
                AudioFormat fmt = file.getAudioFormat();
                f1 = new AudioFile(fmt, starting);
                f2 = new AudioFile(AudioFile.copyFormat(fmt), middle);
                f3 = new AudioFile(AudioFile.copyFormat(fmt), ending);

                WaveformViewersContainer container = ((WaveformViewersContainer)selected.getParent());

                container.getChildren().remove(selected);
                WaveformViewer.remove(selected);

                WaveformViewer v1, v2, v3;
                v1 = new WaveformViewer(f1);
                v2 = new WaveformViewer(f2);
                v3 = new WaveformViewer(f3);
                container.addWaveForm(v1);
                container.addWaveForm(v2);
                container.addWaveForm(v3);
                extendContainersSize();
                v2.setLayoutX(v1.getPrefWidth());
                v3.setLayoutX(v1.getPrefWidth() + v2.getPrefWidth());
            }
        }, () -> {
            new Alert(Alert.AlertType.ERROR, "Cannot cut non-selected audio fragment! Please select it first").showAndWait();
        });
    }

    private int ensureAudioFrameSize(int bufferIndex, AudioFormat fmt, boolean start) {
        int frameSize = fmt.getFrameSize();
        while (bufferIndex % frameSize != 0) {
            bufferIndex = start ? bufferIndex + 1 : bufferIndex - 1;
        }
        return bufferIndex;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }
}
