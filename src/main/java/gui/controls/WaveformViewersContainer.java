package gui.controls;

import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import utils.AudioFile;
import utils.MathUtils;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class WaveformViewersContainer extends Pane {


    public WaveformViewersContainer() {
        super();
        var resource = getClass().getResource("/gui/controls/WaveformViewersContainer.fxml");
        var loader = new FXMLLoader(resource);
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setupEvents();
    }

    private void setupEvents() {
        this.setOnDragOver(e -> {
           if (e.getGestureSource() != this && e.getDragboard().hasFiles()) {
               e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
           }
           e.consume();
        });
        this.setOnDragDropped(e -> {
            var dragboard = e.getDragboard();
            var isSuccess = false;
            if (dragboard.hasFiles() && dragboard.getFiles().size() == 1) {
                var file = dragboard.getFiles().get(0);
                try {
                    var audioFile = new AudioFile(file.getAbsolutePath());
                    var viewer = new WaveformViewer(audioFile);
                    viewer.setLayoutX(e.getX());
                    viewer.setLayoutY(0d);
                    addWaveForm(viewer);
                } catch (IOException | UnsupportedAudioFileException ex) {
                    ex.printStackTrace();
                }
//                if (result.isPresent() && result.get().equals(ButtonType.YES)) {
//                    var path = System.getProperty("user.dir") + File.separator + "TempAudioFiles" + File.separator + file.getName();
//                    var destFile = new File(path);
//                    try {
//                        FileUtils.copyFile(file, destFile);
//                        file = destFile;
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//
//                try {
//                    var provider = ProviderFactory.fromFile(file);
//                    var viewer = new WaveformViewer(provider);
//                    viewer.setLayoutX(e.getX());
//                    viewer.setLayoutY(0d);
//                    addWaveForm(viewer);
//                } catch (IOException | UnsupportedAudioFileException ex) {
//                    ex.printStackTrace();
//                }

                isSuccess = true;
            }
            e.setDropCompleted(isSuccess);
            e.consume();
        });
    }

    private double dragDeltaX = -1d;

    //sets up the position change events
    private void setupWaveformViewerEvents(WaveformViewer viewer) {
        viewer.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> dragDeltaX = viewer.getLayoutX() - e.getSceneX());
        viewer.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if (e.isPrimaryButtonDown()) { //get mouse X and set it there
                double x = MathUtils.clamp(e.getSceneX() + dragDeltaX, 0d, getWidth() - viewer.getWidth());
                viewer.setLayoutX(x);
            }
        });
    }

    public void addWaveForm(WaveformViewer wv) {
        setupWaveformViewerEvents(wv);
        getChildren().add(wv);
        wv.clear();
        var newWidth = getChildren().
                stream().
                mapToDouble(child -> ((WaveformViewer)child).getPrefWidth()).
                sum();
        setMinWidth(newWidth);
        setPrefWidth(newWidth);
        wv.invalidate();
    }
}
