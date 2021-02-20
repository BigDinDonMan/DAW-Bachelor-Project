package utils;

import lombok.Getter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SoundMixer {

    private List<SoundClip> clipsToMix;
    private float compressionMultiplier = 0.8f;

    public SoundMixer() {
        clipsToMix = new ArrayList<>();
    }

    public SoundMixer(float multiplier) {
        this();
        compressionMultiplier = multiplier;
    }

    public void addClip(SoundClip c) {
        clipsToMix.add(c);
    }

    public void addClip(String path) {
        try {
            clipsToMix.add(new SoundClip(path));
        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    public void addClips(SoundClip... clips) {
        clipsToMix.addAll(Arrays.asList(clips));
    }

    public void addClips(Collection<? extends SoundClip> clips) {
        clipsToMix.addAll(clips);
    }

    public SoundClip mix() {
        if (this.clipsToMix.isEmpty()) {
            throw new IllegalStateException("There are no clips to mix.");
        }
        if (this.clipsToMix.size() == 1) {
            return this.clipsToMix.get(0);
        }
        var currentClip = this.clipsToMix.get(0);
        for (int i = 1; i < this.clipsToMix.size(); ++i) {
            var secondClip = this.clipsToMix.get(i);
            var currentSamples = currentClip.getSamples();
            var secondSamples = secondClip.getSamples();

            for (int j = 0; j < currentSamples.length; ++j) {
                var sample1 = (short)(currentSamples[j] * Short.MAX_VALUE) * compressionMultiplier;
                var sample2 = (short)(secondSamples[j] * Short.MAX_VALUE) * compressionMultiplier;
                var resultSample = sample1 + sample2;
                currentSamples[j] = MathUtils.clamp(resultSample / (float)Short.MAX_VALUE, -1f, 1f);
            }
        }

        return new SoundClip(currentClip.getSamples(), currentClip.getAudioFormat());
    }
}
