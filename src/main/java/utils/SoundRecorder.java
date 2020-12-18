package utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicBoolean;

public class SoundRecorder implements Runnable {

    private TargetDataLine sourceLine;
    private File outputFile;
    private AtomicBoolean stopped;
    private AudioFormat recordingFormat;

    public SoundRecorder(AudioFormat fmt, TargetDataLine sourceLine, File outputFile) {
        this.sourceLine = sourceLine;
        this.outputFile = outputFile;
        this.stopped = new AtomicBoolean();
        this.recordingFormat = fmt;
    }

    @Override
    public void run() {
        stopped.set(false);
        record();
    }

    private void record() {
        File tempFile = null;
        try {
            tempFile = Files.createTempFile("tmprec", "wav").toFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            byte[] tempBuffer = new byte[sourceLine.getBufferSize() / 4];
            sourceLine.open(recordingFormat);
            sourceLine.start();
            while (!stopped.get()) {
                int read = sourceLine.read(tempBuffer, 0, tempBuffer.length);
                if (read == -1) break;
                out.write(tempBuffer, 0, read);
            }
            var ais = new AudioInputStream(
                    new FileInputStream(tempFile),
                    recordingFormat,
                    tempFile.length() / recordingFormat.getFrameSize()
            );
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, outputFile);
            ais.close();
            tempFile.delete();
        } catch (IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public synchronized void stop() {
        stopped.set(true);
    }
}
