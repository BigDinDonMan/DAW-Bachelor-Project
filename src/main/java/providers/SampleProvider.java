package providers;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

//normalize all of the samples to IEEE float from range <-1, 1>
public abstract class SampleProvider {

    protected int dataSize = 1;
    protected AudioFormat audioFormat;
    protected AudioInputStream sourceStream;
    protected File sourceFile;

    public SampleProvider(File sourceFile, AudioFormat fmt, AudioInputStream sourceStream) throws IOException {
        this.sourceFile = sourceFile;
        this.audioFormat = fmt;
        this.sourceStream = sourceStream;
    }

    public int availableSamples() throws IOException {
        return this.sourceStream.available() / this.dataSize;
    }

    public AudioFormat getAudioFormat() {
        return this.audioFormat;
    }

    public void reset() throws IOException, UnsupportedAudioFileException {
        this.sourceStream.close();
        var tempStream = AudioSystem.getAudioInputStream(this.sourceFile);
        this.sourceStream = tempStream;
    }

    public abstract int provide(float[] buffer) throws IOException;

    @Override
    protected void finalize() {
        try {
            this.sourceStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
