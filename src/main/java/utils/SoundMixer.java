package utils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SoundMixer {

    private List<SoundClip> clipsToMix;

    public SoundMixer() {
        clipsToMix = new ArrayList<>();
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

    public SoundClip mix(AudioFormat targetFormat) {
        throw new IllegalStateException("Not implemented");
    }
}
