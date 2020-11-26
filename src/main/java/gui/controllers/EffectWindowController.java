package gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utils.SoundClip;

public abstract class EffectWindowController {

    protected SoundClip file;
    protected int bufferStartPoint;
    protected int bufferEndPoint;
    @FXML
    protected AnchorPane root;

    protected abstract void applyEffect() throws Exception;

    public SoundClip getAudioFile() {
        return file;
    }

    public void setAudioFile(SoundClip f) {
        this.file = f;
        setEditBounds(0, f.getSamples().length);
    }

    public void setAudioFile(SoundClip f, int start, int end) {
        setAudioFile(f);
        setEditBounds(start, end);
    }

    public void setEditBounds(int start, int end) {
        this.bufferStartPoint = start;
        this.bufferEndPoint = end;
    }

    @FXML
    public void closeWindow() {
        ((Stage)root.getScene().getWindow()).close();
    }
}
