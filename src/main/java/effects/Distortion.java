package effects;

import lombok.AllArgsConstructor;
import utils.MathUtils;


@AllArgsConstructor
public class Distortion implements SoundEffect {

    private float gain;

    @Override
    public void apply(float[] buffer, int offset, int len) {
        for (int i = offset; i < offset + len; ++i) {
            float x = buffer[i];
            float absX = Math.abs(x);
            buffer[i] = (float)((x / absX) * (1f - Math.pow(Math.E, (x * x) / absX))) * gain;
            buffer[i] = MathUtils.clamp(buffer[i], -1f, 1f);
        }
    }
}
