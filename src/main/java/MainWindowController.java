import gui.controllers.RecordingWindowController;
import gui.controls.WaveformViewer;
import gui.controls.WaveformViewersContainer;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;
import org.kordamp.bootstrapfx.BootstrapFX;
import utils.ArrayUtils;
import utils.AudioPlayer;
import utils.SoundClip;
import utils.SoundSelectionMapper;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


//todo: set new track container's width to the width of root
public class MainWindowController implements Initializable {

    @Getter
    @Setter
    private Stage mainStage;

    @FXML
    private ScrollPane waveformViewersContainer;
    @FXML
    private VBox waveformsVBox;
    @FXML
    private Button playButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button stopButton;
    @FXML
    private CheckBox loopingCheckBox;

    private Line playbackPointerLine;
    private Timeline playbackTimeline;

    private SoundClip cachedTimelineClip;

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
                    containers.addAll(added.stream().filter(n -> n instanceof WaveformViewersContainer).map(n -> (WaveformViewersContainer)n).collect(Collectors.toList()));
                } else if (change.wasRemoved()) {
                    var removed = change.getRemoved();
                    containers.removeAll(removed.stream().filter(n -> n instanceof WaveformViewersContainer).map(n -> (WaveformViewersContainer)n).collect(Collectors.toList()));
                }
            }
        });
        try {
            SoundClip clip = new SoundClip(System.getProperty("user.dir") + File.separator + "00_otusznje.wav");
//            Echo e = new Echo(clip.getAudioFormat(), 10, TimeUnit.MILLISECONDS, 3);
//            e.apply(clip.getSamples());
//            Distortion dist = new Distortion(2.5f);
//            dist.apply(clip.getSamples());
//            Overdrive overdrive = new Overdrive();
//            overdrive.apply(clip.getSamples());
//            SoundClip toUpsample = new SoundClip(System.getProperty("user.dir") + File.separator + "test.wav");
//            var targetFormat = new AudioFormat(44100, 16, 2, true, false);
//            Resampler resampler = new Resampler(toUpsample.getAudioFormat(), targetFormat);
//            SoundClip resampled = new SoundClip(resampler.apply(toUpsample.getSamples()), targetFormat);
//            SoundFileExporter exporter = new SoundFileExporter(resampled);
//            exporter.export(System.getProperty("user.dir") + File.separator + "resampled.wav");
            WaveformViewersContainer container = new WaveformViewersContainer();
            WaveformViewer viewer = new WaveformViewer(clip);
            waveformsVBox.getChildren().add(container);
            container.addWaveForm(viewer);
        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        stopButton.setDisable(true);
        pauseButton.setDisable(true);

        playbackPointerLine = new Line();
        playbackPointerLine.setStrokeWidth(2.5);
        playbackPointerLine.setStroke(Color.RED);
        playbackTimeline = new Timeline();
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

    @FXML
    private void showRecordingWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/RecordingWindow.fxml"));
        try {
            Parent p = loader.load();
            RecordingWindowController controller = loader.getController();
            Scene s = new Scene(p);
            s.getStylesheets().addAll(
                    BootstrapFX.bootstrapFXStylesheet(),
                    getClass().getClassLoader().getResource("styles/app-style.css").toExternalForm()
            );
            Stage stage = new Stage();
            stage.setOnCloseRequest(e -> controller.stopRecording());
            stage.setScene(s);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Recording properties");
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private AudioPlayer audioPlayer;


    @FXML
    private void playTimeline() {
        if (audioPlayer == null) {
            audioPlayer = new AudioPlayer();
            audioPlayer.addPlaybackListener(e -> {
                var type = e.getType();
                boolean condition = type.equals(LineEvent.Type.CLOSE) || type.equals(LineEvent.Type.STOP);
                playButton.setDisable(!condition);
                pauseButton.setDisable(condition);
                stopButton.setDisable(condition);
                loopingCheckBox.setDisable(!condition);
            });
        }
        //if selection is present, play just the selection
        //else play everything
        WaveformViewer selected = WaveformViewer.getSelected();
        if (selected != null) { //fixme: pass sound clip that corresponds to selection bounds to the audioplayer
            if (selected.getSoundClip() != audioPlayer.getSoundClip()) {
                audioPlayer.setSoundClip(selected.getSoundClip());
            }
            containers.stream().flatMap(c -> c.getChildren().stream().map(wv -> (WaveformViewer)wv)).forEach(wv -> wv.getChildren().remove(playbackPointerLine));
            selected.getChildren().add(playbackPointerLine);
            playbackPointerLine.setStartY(0);
            playbackPointerLine.setEndY(selected.getHeight());
            var loop = loopingCheckBox.isSelected();
            playbackTimeline.setCycleCount(loop ? Animation.INDEFINITE : 1);
            audioPlayer.setLooping(loop);
            if (playbackTimeline.getStatus() == Animation.Status.STOPPED) {
                playbackTimeline.getKeyFrames().clear();
                var targetVal = selected.getLayoutX() + selected.getPrefWidth();
                playbackTimeline.getKeyFrames().add(new KeyFrame(
                        Duration.millis(selected.getSoundClip().getDurationInMillis()),
                        new KeyValue(playbackPointerLine.startXProperty(), targetVal),
                        new KeyValue(playbackPointerLine.endXProperty(), targetVal)
                ));
            }
            playbackTimeline.setOnFinished(e -> {
                selected.getChildren().remove(playbackPointerLine);
                playbackPointerLine.setStartX(0);
                playbackPointerLine.setEndX(0);
            });
            audioPlayer.play();
            playbackTimeline.play();
        } else {
            if (cachedTimelineClip == null) {
//                TimelineToClipMapper mapper = new TimelineToClipMapper(, containers);
//                SoundClip clip = mapper.map();
//                cachedTimelineClip = clip;
//                audioPlayer.setSoundClip(clip);
//                audioPlayer.play();
            } else {
//                if (cachedTimelineClip != audioPlayer.getSoundClip()) {
//                    audioPlayer.setSoundClip(cachedTimelineClip);
//                }
//                audioPlayer.play();
            }

        }
    }

    @FXML
    private void pauseTimeline() {
        if (audioPlayer.isPlaying()) {
            audioPlayer.pause();
            playbackTimeline.pause();
        }
    }

    @FXML
    private void stopTimeline() {
        if (audioPlayer.isPlaying()) {
            audioPlayer.reset();
            playbackTimeline.stop();
            playbackPointerLine.setStartX(0);
            playbackPointerLine.setEndX(0);
        }
    }

    @FXML
    private void showExportDialog() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ExportAudioFileWindow.fxml"));
        try {
            loader.setControllerFactory(callback -> new ExportAudioFileWindowController(containers));
            Parent p = loader.load();
            Stage stage = new Stage();
            Scene s = new Scene(p);
            s.getStylesheets().addAll(
                    BootstrapFX.bootstrapFXStylesheet(),
                    getClass().getClassLoader().getResource("styles/app-style.css").toExternalForm()
            );
            stage.setTitle("Export clip");
            stage.setScene(s);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            double start = bounds.getValue0();
            double end = bounds.getValue1();
            SoundClip file = selected.getSoundClip();
            float[] samples = file.getSamples();
            SoundSelectionMapper mapper = new SoundSelectionMapper(selected.getSamplesPerPixel(), file.getAudioFormat().getFrameSize());
            Pair<Integer, Integer> bufferBounds = mapper.map(start, end);
            if ((int)start == 0 && (int)end == (int)selected.getWidth()) return;
            if ((int)start == 0 || (int)end == (int)selected.getWidth()) {//split viewer into 2 from the start
                float[] first = ArrayUtils.slice(samples, bufferBounds.getValue0(), bufferBounds.getValue1());
                float[] second = ArrayUtils.slice(samples, bufferBounds.getValue1(), samples.length);
                SoundClip f1, f2;
                AudioFormat fmt = file.getAudioFormat();
                f1 = new SoundClip(first, file.getAudioFormat());
                f2 = new SoundClip(second, SoundClip.copyFormat(fmt));

                WaveformViewersContainer container = ((WaveformViewersContainer)selected.getParent());

                container.getChildren().remove(selected);
                WaveformViewer.remove(selected);

                WaveformViewer v1, v2;
                v1 = new WaveformViewer(f1);
                v2 = new WaveformViewer(f2);

                container.addWaveForms(v1, v2);

                v2.setLayoutX(v1.getPrefWidth());
                extendContainersSize();

            } else { //cut it into 3 viewers
                //todo: fix adding the items, if you cut the sound at the end it appends as if container was empty
                float[] starting = ArrayUtils.slice(samples, 0, bufferBounds.getValue0());
                float[] middle = ArrayUtils.slice(samples, bufferBounds.getValue0(), bufferBounds.getValue1());
                float[] ending = ArrayUtils.slice(samples, bufferBounds.getValue1(), samples.length);
                SoundClip f1, f2, f3;
                AudioFormat fmt = file.getAudioFormat();
                f1 = new SoundClip(starting, fmt);
                f2 = new SoundClip(middle, SoundClip.copyFormat(fmt));
                f3 = new SoundClip(ending, SoundClip.copyFormat(fmt));

                WaveformViewersContainer container = ((WaveformViewersContainer)selected.getParent());

                container.getChildren().remove(selected);
                WaveformViewer.remove(selected);

                WaveformViewer v1, v2, v3;
                v1 = new WaveformViewer(f1);
                v2 = new WaveformViewer(f2);
                v3 = new WaveformViewer(f3);
                container.addWaveForms(v1, v2, v3);
                extendContainersSize();
                v2.setLayoutX(v1.getPrefWidth());
                v3.setLayoutX(v1.getPrefWidth() + v2.getPrefWidth());
            }
        }, () -> {
            new Alert(Alert.AlertType.ERROR, "Cannot cut non-selected audio fragment! Please select it first").showAndWait();
        });
    }

    @FXML
    private void loadFile() {
        var fileChooser = new FileChooser();
        FileChooser.ExtensionFilter wavFilter = new FileChooser.ExtensionFilter("WAV sound clip (*.wav)", "*.wav");
        fileChooser.getExtensionFilters().add(wavFilter);
        var file = fileChooser.showOpenDialog(mainStage);
        if (file == null) return;
        try {
            var clip = new SoundClip(file.getAbsolutePath());
            WaveformViewersContainer container = new WaveformViewersContainer();
            WaveformViewer wv = new WaveformViewer(clip);
            containers.add(container);
            waveformsVBox.getChildren().add(container);
            container.addWaveForm(wv);
        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }
}
