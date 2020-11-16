package providers;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Int16SampleProvider extends SampleProvider {

    public Int16SampleProvider(File sourceFile, AudioFormat fmt, AudioInputStream sourceStream) throws IOException {
        super(sourceFile, fmt, sourceStream);
        this.dataSize = 2;
    }

    @Override
    public int provide(float[] buffer) throws IOException {
        int framesToRead = buffer.length / this.audioFormat.getFrameSize();
        byte[] readBuffer = new byte[this.audioFormat.getFrameSize()];
        int bufferIndex = 0;
        int i = 0;
        for (; i < framesToRead; ++i) {
            int read = sourceStream.read(readBuffer, 0, readBuffer.length);
            if (read == -1) break;
            ByteBuffer bb = ByteBuffer.wrap(readBuffer);
            for (int j = 0; j < read; j += dataSize) {
                buffer[bufferIndex++] = bb.getShort(j) / (float)Short.MAX_VALUE;
            }
        }
        return i * audioFormat.getFrameSize();
    }


}
