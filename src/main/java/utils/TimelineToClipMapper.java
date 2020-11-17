package utils;

import gui.controls.WaveformViewersContainer;

import javax.sound.sampled.AudioFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

//todo: after you map it for playback, store it until any change is made (in case someone wants to export it right away)
public class TimelineToClipMapper {

    private List<WaveformViewersContainer> timelineContainers;
    private AudioFormat audioFormat;

    public TimelineToClipMapper(AudioFormat targetFormat, WaveformViewersContainer... containers) {
        this(targetFormat, Arrays.asList(containers));
    }

    public TimelineToClipMapper(AudioFormat targetFormat, Collection<? extends WaveformViewersContainer> containers) {
        timelineContainers = new ArrayList<>(containers);
        audioFormat = targetFormat;
    }

    public SoundClip map() {
        //convert every container's sound clips into a buffer
        for (var container: timelineContainers) {

        }

        SoundMixer mixer = new SoundMixer();

        //perform up/downsampling of clips
        //basically convert them all to the same format

        return mixer.mix(audioFormat);
    }
}
