package utils;

import org.javatuples.Pair;

public class SoundSelectionMapper {

    private int samplesPerPixel;
    private int frameSize;

    public SoundSelectionMapper(int frameSize, int samplesPerPixel) {
        this.samplesPerPixel = samplesPerPixel;
        this.frameSize = frameSize;
    }

    public Pair<Integer, Integer> map(double selectionStart, double selectionEnd) {
        int start = (int)(selectionStart * this.samplesPerPixel);
        int end = (int)(selectionEnd * this.samplesPerPixel);
        start = ensureAudioFrameSize(start, true);
        end = ensureAudioFrameSize(end, false);
        return new Pair<>(start, end);
    }

    private int ensureAudioFrameSize(int bufferIndex, boolean increment) {
        while (bufferIndex % this.frameSize != 0) {
            bufferIndex = increment ? bufferIndex + 1 : bufferIndex - 1;
        }
        return bufferIndex;
    }
}
