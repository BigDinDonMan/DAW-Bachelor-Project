package processing;


import org.apache.commons.lang3.NotImplementedException;
import utils.SoundClip;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Resampler implements Processing {

    private AudioFormat sourceFormat;
    private AudioFormat targetFormat;

    public Resampler(AudioFormat sourceFormat, AudioFormat targetFormat) {
        if (!AudioSystem.isConversionSupported(targetFormat, sourceFormat)) {
            throw new IllegalArgumentException(
                    String.format("Conversion between these formats is not supported. \nSource format: %s\nTarget format: %s",  sourceFormat.toString(), targetFormat.toString())
            );
        }
        this.sourceFormat = sourceFormat;
        this.targetFormat = targetFormat;
    }

    @Override
    public float[] apply(float[] buffer, int offset, int len) {
        throw new NotImplementedException("Resampler only supports whole files");
    }

    @Override
    public float[] apply(float[] buffer){
        byte[] bytes = convertToBytes(buffer, sourceFormat);
        try (var inputStream = new AudioInputStream(new ByteArrayInputStream(bytes), sourceFormat, bytes.length);
             var converted = AudioSystem.getAudioInputStream(targetFormat, inputStream)) {
            File tmp = File.createTempFile("tmp-resampled",null);
            AudioSystem.write(converted, AudioFileFormat.Type.WAVE, tmp);
            SoundClip c = new SoundClip(tmp.getAbsolutePath());
            tmp.delete();
            return c.getSamples();
        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace(System.err);
        }
        return new float[0];
    }

    private byte[] convertToBytes(float[] buffer, AudioFormat fmt) {
        int sampleSize = fmt.getSampleSizeInBits() / 8;
        var tempBuffer = new byte[buffer.length * sampleSize];
        for (int i = 0, bufIndex = 0; i < buffer.length; ++i) {
            switch (sampleSize) {
                case 1:{
                    byte b = (byte)(buffer[i] * Byte.MAX_VALUE);
                    tempBuffer[bufIndex++] = b;
                    break;
                }

                case 2: {
                    short s = (short)(buffer[i] * Short.MAX_VALUE);
                    ByteBuffer bb = ByteBuffer.allocate(2).order(fmt.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
                    var bytes = bb.putShort(s).array();
                    tempBuffer[bufIndex++] = bytes[0];
                    tempBuffer[bufIndex++] = bytes[1];
                    break;
                }

                case 4: {
                    float f = buffer[i];
                    ByteBuffer bb_f = ByteBuffer.allocate(4).order(fmt.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
                    var f_bytes = bb_f.putFloat(f).array();
                    for (int j = 0; j < 4; ++j) {
                        tempBuffer[bufIndex++] = f_bytes[j];
                    }
                    break;
                }
            }
        }
        return tempBuffer;
    }
}
