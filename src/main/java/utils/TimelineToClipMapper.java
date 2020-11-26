package utils;

import gui.controls.WaveformViewer;
import gui.controls.WaveformViewersContainer;

import javax.sound.sampled.AudioFormat;
import java.util.*;
import java.util.stream.Collectors;

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
        //todo: set clip buffer size to the furthest mapped waveform viewer position
        if (this.timelineContainers.isEmpty()) return null;
        List<SoundClip> clips = new ArrayList<>();
        int spp = 96;
        int clipBufferUnifiedSize = (int)Math.ceil(
                this.timelineContainers.stream().map(c -> c.getLayoutX() + c.getPrefWidth()).max(Comparator.naturalOrder()).get() * spp);
        while (clipBufferUnifiedSize % audioFormat.getFrameSize() != 0) {
            clipBufferUnifiedSize++;
        }
        for (var container: timelineContainers) {
            var viewers = container.getChildren().stream().
                    map(child -> (WaveformViewer) child).sorted(Comparator.comparing(WaveformViewer::getLayoutX)).collect(Collectors.toList());
            if (viewers.isEmpty()) continue;
            float[] clipBuffer = new float[clipBufferUnifiedSize];
            for (var viewer: viewers) {
                int start = (int)viewer.getLayoutX() * spp;
                float[] samples = viewer.getSoundClip().getSamples();
                for (int i = start, samplesIndex = 0; samplesIndex < samples.length && i < clipBuffer.length; ++i, ++samplesIndex) {
                    clipBuffer[i] = samples[samplesIndex];
                }
            }
            clips.add(new SoundClip(audioFormat, clipBuffer));
        }

        SoundMixer mixer = new SoundMixer();
        mixer.addClips(clips);
        //perform up/downsampling of clips
        //basically convert them all to the same format

        return mixer.mix();
    }
}
