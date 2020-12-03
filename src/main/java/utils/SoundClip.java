package utils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SoundClip {

    private AudioFormat audioFormat;
    private float[] samples;

    public SoundClip(String path) throws IOException, UnsupportedAudioFileException {
        AudioInputStream stream = AudioSystem.getAudioInputStream(new File(path));
        this.audioFormat = stream.getFormat();

        this.samples = new float[stream.available()];
        int index = 0;
        try (var audioStream = stream) {

            byte[] tempBuffer = new byte[audioFormat.getFrameSize()];

            while (audioStream.available() > 0) {
                int read = audioStream.read(tempBuffer);
                if (read == -1) break;
                ByteBuffer bb = ByteBuffer.wrap(tempBuffer).order(ByteOrder.nativeOrder());
                switch (audioFormat.getSampleSizeInBits() / 8) {
                    case 1:
                        for (int i = 0; i < read; ++i) {
                            float sample = tempBuffer[i] / (float)Byte.MAX_VALUE;
                            samples[index++] = sample;
                        }
                        break;
                    case 2:
                        for (int i = 0; i < read; i += 2) {
                            float sample = bb.getShort(i) / (float)Short.MAX_VALUE;
                            samples[index++] = sample;
                        }
                        break;
                    case 4:
                        for (int i = 0; i < read; i += 4) {
                            float sample = bb.getFloat(i);
                            samples[index++]=sample;
                        }
                        break;
                }
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public SoundClip(AudioFormat fmt, float[] samples) {
        this.samples = samples;
        this.audioFormat = fmt;
    }

    public long getDurationInMillis() {
        var samplesPerSecond = this.audioFormat.getChannels() * this.audioFormat.getSampleRate();
        return (long)((this.samples.length / samplesPerSecond) * 1000L);
    }

    public float[] getSamples() {
        return this.samples;
    }

    public AudioFormat getAudioFormat() {
        return this.audioFormat;
    }

    public static AudioFormat copyFormat(AudioFormat fmt) {
        return new AudioFormat(
                fmt.getEncoding(),
                fmt.getSampleRate(),
                fmt.getSampleSizeInBits(),
                fmt.getChannels(),
                fmt.getFrameSize(),
                fmt.getFrameRate(),
                fmt.isBigEndian()
        );
    }

    public static AudioFormat copyFormat(SoundClip f) {
        return copyFormat(f.getAudioFormat());
    }
}
