package utils;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public class SoundRecorder implements Runnable {

    private AudioFormat recordingFormat = new AudioFormat(
            8000,
            16,
            1,
            true,
            ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN
    );// this format ensures we get default system mic

    private TargetDataLine sourceLine;
    private File outputFile;
    private AtomicBoolean stopped;

    public SoundRecorder(TargetDataLine sourceLine, File outputFile) {
        this.sourceLine = sourceLine;
        this.outputFile = outputFile;
        this.stopped = new AtomicBoolean();
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
                System.out.println("bytes recorded: " + read);
                if (read == -1) continue;
                out.write(tempBuffer, 0, read);
            }
            var ais = new AudioInputStream(
                    new FileInputStream(tempFile),
                    recordingFormat,
                    tempFile.length() / recordingFormat.getFrameSize()
            );
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, outputFile);
            tempFile.delete();
        } catch (IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public synchronized void stop() {
        stopped.set(true);
    }
}
