package utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Getter
@AllArgsConstructor
public class SoundClip {

    private float[] samples;
    private AudioFormat audioFormat;

    public static final AudioFormat DEFAULT_FORMAT = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            44100,
            16,
            2,
            4,
            44100,
            false
    );

    public SoundClip(String path) throws IOException, UnsupportedAudioFileException {
        AudioInputStream stream = AudioSystem.getAudioInputStream(new File(path));
        this.audioFormat = stream.getFormat();

        int samplesCount = stream.available() / (audioFormat.getSampleSizeInBits() / 8);

        this.samples = new float[samplesCount];
        int index = 0;
        try (var audioStream = stream) {

            byte[] tempBuffer = new byte[audioFormat.getFrameSize()];

            while (audioStream.available() > 0) {
                int read = audioStream.read(tempBuffer);
                if (read == -1) break;
                ByteOrder order = audioFormat.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
                ByteBuffer bb = ByteBuffer.wrap(tempBuffer).order(order);
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

    public long getDurationInMillis() {
        var samplesPerSecond = this.audioFormat.getChannels() * this.audioFormat.getSampleRate();
        return (long)((this.samples.length / samplesPerSecond) * 1000L);
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
