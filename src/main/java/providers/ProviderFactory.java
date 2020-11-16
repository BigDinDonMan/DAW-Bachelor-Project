package providers;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class ProviderFactory {

    private ProviderFactory() {}

    public static SampleProvider fromFile(String path) throws IOException, UnsupportedAudioFileException {
        return fromFile(new File(path));
    }

    public static SampleProvider fromFile(File f) throws IOException, UnsupportedAudioFileException {
        AudioInputStream audioFileStream = AudioSystem.getAudioInputStream(f);
        AudioFormat fmt = audioFileStream.getFormat();
        SampleProvider provider = null;
        switch (fmt.getSampleSizeInBits()) {
            case 8:
                provider = new Int8SampleProvider(f, fmt, audioFileStream);
                break;
            case 16:
                provider = new Int16SampleProvider(f, fmt, audioFileStream);
                break;
            case 32:
                provider = new Float32SampleProvider(f, fmt, audioFileStream);
                break;
        }
        return provider;
    }
}
