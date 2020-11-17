package processing;

import utils.SoundClip;

import javax.sound.sampled.AudioFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChannelSplit {

    private SoundClip file;

    public ChannelSplit(SoundClip f) {
        this.file = f;
    }

    public List<SoundClip> split() {
        if (file == null) throw new NullPointerException("Sound file was null.");
        final float[] samples = file.getSamples();
        final AudioFormat fmt = file.getAudioFormat();
        List<SoundClip> result = new ArrayList<>();
        final int channels = fmt.getChannels();
        if (channels == 1) {
            return Collections.singletonList(file);
        }
        for (int currentChannels = 0; currentChannels < channels; ++currentChannels) {
            float[] currentBuffer = new float[samples.length / channels];
            for (int i = currentChannels, bufferIndex = 0; bufferIndex < currentBuffer.length; i += channels, bufferIndex++) {
                currentBuffer[bufferIndex] = samples[i];
            }
            SoundClip f = new SoundClip(
                    new AudioFormat(
                            fmt.getEncoding(),
                            fmt.getSampleRate(),
                            fmt.getSampleSizeInBits(),
                            1,
                            fmt.getFrameSize() / channels,
                            fmt.getFrameRate(),
                            fmt.isBigEndian()
                    ),
                    currentBuffer);
            result.add(f);
        }
        return result;
    }
}
