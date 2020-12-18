package effects;

import lombok.AllArgsConstructor;
import utils.MathUtils;

import javax.sound.sampled.AudioFormat;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class Delay implements SoundEffect {

    private AudioFormat audioFormat;
    private int timeValue;
    private TimeUnit timeUnit;

    @Override
    public void apply(float[] buffer, int offset, int length) {
        long delay = timeUnit.toMillis(timeValue);
        int delayInSamples = (int)(delay / 1000f * audioFormat.getSampleRate());
        for (int i = offset, delayPtr = offset + delayInSamples;  i < offset + length; ++i, delayPtr = (delayPtr + 1) % buffer.length) {
            float currentSample = buffer[i];
            float currentDelaySample = buffer[delayPtr];
            final float finalValue = MathUtils.clamp(currentSample + currentDelaySample, -1f, 1f);
            buffer[i] = finalValue;
        }
    }
}
