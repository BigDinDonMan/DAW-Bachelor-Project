package gui.controllers;

import utils.AudioFile;

public abstract class EffectWindowController {

    protected AudioFile file;
    protected int bufferStartPoint;
    protected int bufferEndPoint;

    protected abstract void applyEffect() throws Exception;

    public AudioFile getAudioFile() {
        return file;
    }

    public void setAudioFile(AudioFile f) {
        this.file = f;
        setEditBounds(0, f.getSamples().length);
    }

    public void setAudioFile(AudioFile f, int start, int end) {
        setAudioFile(f);
        setEditBounds(start, end);
    }

    public void setEditBounds(int start, int end) {
        this.bufferStartPoint = start;
        this.bufferEndPoint = end;
    }
}
