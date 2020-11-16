package providers;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.io.IOException;

public class Int8SampleProvider extends SampleProvider {


    public Int8SampleProvider(File sourceFile, AudioFormat fmt, AudioInputStream sourceStream) throws IOException {
        super(sourceFile, fmt, sourceStream);
        dataSize = 1;
    }

    @Override
    public int provide(float[] buffer) throws IOException {
        int framesToRead = buffer.length / audioFormat.getFrameSize();
        byte[] readBuffer = new byte[audioFormat.getFrameSize()];
        int bufferIndex = 0;
        int i = 0;
        for (; i < framesToRead; ++i) {
            int read = sourceStream.read(readBuffer, 0, readBuffer.length);
            if (read == -1) break;
            for (int j = 0; j < read; j += dataSize) {
                buffer[bufferIndex++] = readBuffer[j] / (float)Byte.MAX_VALUE;
            }
        }
        return i * audioFormat.getFrameSize();
    }

}
