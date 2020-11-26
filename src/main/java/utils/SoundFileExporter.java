package utils;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SoundFileExporter {

    private SoundClip clip;

    public SoundFileExporter(SoundClip c) {
        this.clip = c;
    }

    public void export(String path) throws IOException {
        File f = new File(path);
        float[] samples = clip.getSamples();
        var sampleSize = clip.getAudioFormat().getSampleSizeInBits() / 8;
        byte[] bytes = new byte[samples.length * sampleSize];
        int currentIndex = 0;
        byte[] sampleBytes;
        switch (sampleSize) {
            case 1:
                for (int i = 0; i < samples.length; ++i) {
                    bytes[i] = (byte)(samples[i] * Byte.MAX_VALUE);
                }
                break;
            case 2:
                for (float sample : samples) {
                    ByteBuffer bb = ByteBuffer.allocate(sampleSize).order(ByteOrder.nativeOrder());
                    bb.putShort((short) (sample * Short.MAX_VALUE));
                    sampleBytes = bb.array();
                    for (int j = 0; j < sampleSize; ++j) {
                        bytes[currentIndex++] = sampleBytes[j];
                    }
                }
                break;
            case 4:
                for (float sample : samples) {
                    ByteBuffer _bb = ByteBuffer.allocate(sampleSize).order(ByteOrder.nativeOrder());
                    _bb.putFloat(sample);
                    sampleBytes = _bb.array();
                    for (int j = 0; j < sampleSize; ++j) {
                        bytes[currentIndex++] = sampleBytes[j];
                    }
                }
                break;
        }
        try (var stream = new AudioInputStream(new ByteArrayInputStream(bytes), clip.getAudioFormat(), bytes.length)) {
            AudioSystem.write(stream, AudioFileFormat.Type.WAVE, f);
        }
    }
}
