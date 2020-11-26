package effects;

import utils.MathUtils;

import javax.sound.sampled.AudioFormat;

public class Flanger implements SoundEffect {

    private float sweepFrequency;
    private int delay;
    private int sweepRange;
    private AudioFormat audioFormat;

    public Flanger(AudioFormat fmt, float freq, int delay, int range) {
        this.sweepFrequency = freq;
        this.delay = delay;
        this.sweepRange = range;
        this.audioFormat = fmt;
    }

    @Override
    public void apply(float[] buffer, int offset, int length) {
        for (int i = offset; i < (length - offset) - delay - sweepRange; ++i) {
            int index = i + delay + sweepRange + (int)Math.round(sweepRange * Math.sin(2 * i * Math.PI * sweepFrequency / audioFormat.getSampleRate()));
            float finalVal = buffer[i] + buffer[MathUtils.clamp(index, 0, (length - offset) - 1)];
            buffer[i] = MathUtils.clamp(finalVal, -1f, 1f);
        }
    }
}
