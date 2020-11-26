package effects;

import utils.MathUtils;

import javax.sound.sampled.AudioFormat;
import java.util.concurrent.TimeUnit;

public class Delay implements SoundEffect {

    private AudioFormat audioFormat;
    private TimeUnit timeUnit;
    private int timeValue;

    public Delay(AudioFormat fmt, int value, TimeUnit unit) {
        this.audioFormat = fmt;
        this.timeValue = value;
        this.timeUnit = unit;
    }

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
