package utils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SoundMixer {

    private List<SoundClip> clipsToMix;

    public SoundMixer() {
        clipsToMix = new ArrayList<>();
    }

    public void addClip(SoundClip f) {
        clipsToMix.add(f);
    }

    public void addClip(String path) {
        try {
            clipsToMix.add(new SoundClip(path));
        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    public SoundClip mix(AudioFormat targetFormat) {
        throw new IllegalStateException("Not implemented");
    }
}
