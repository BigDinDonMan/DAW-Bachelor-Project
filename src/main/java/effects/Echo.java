package effects;

import lombok.AllArgsConstructor;
import utils.MathUtils;

import javax.sound.sampled.AudioFormat;
import java.util.concurrent.TimeUnit;

public class Echo extends Delay implements SoundEffect {

    private int repeats;

    public Echo(AudioFormat audioFormat, int timeValue, TimeUnit timeUnit, int repeats) {
        super(audioFormat, timeValue, timeUnit);
        this.repeats = repeats;
    }

    @Override
    public void apply(float[] buffer, int offset, int len) {
        long delay = timeUnit.toMillis(timeValue);
        int delayInSamples = (int)(delay / 1000f * audioFormat.getSampleRate());
        for (int i = 0; i < repeats; ++i) {
            for (int j = offset, delayPtr = offset + delayInSamples * (repeats + 1); j < offset + len; ++j, delayPtr = (delayPtr + 1) % buffer.length) {
                final float currentSample = buffer[j];
                final float currentDelaySample = buffer[delayPtr];
                final float finalValue = MathUtils.clamp(currentDelaySample + currentSample, -1f, 1f);
                buffer[j] = finalValue;
            }
        }
    }
}
