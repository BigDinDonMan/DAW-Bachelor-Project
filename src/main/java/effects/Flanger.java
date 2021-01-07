package effects;

import lombok.AllArgsConstructor;
import utils.MathUtils;

import javax.sound.sampled.AudioFormat;

@AllArgsConstructor
public class Flanger implements SoundEffect {

    private AudioFormat audioFormat;
    private float sweepFrequency;
    private int delay;
    private int sweepRange;

    @Override
    public void apply(float[] buffer, int offset, int length) {
        for (int i = offset; i < (length - offset) - delay - sweepRange; ++i) {
            final int index = i + delay + sweepRange + (int)Math.round(sweepRange * Math.sin(2 * i * Math.PI * sweepFrequency / audioFormat.getSampleRate()));
            final float finalVal = buffer[i] + buffer[MathUtils.clamp(index, offset, (length - offset) - 1)];
            buffer[i] = MathUtils.clamp(finalVal, -1f, 1f);
        }
    }
}
