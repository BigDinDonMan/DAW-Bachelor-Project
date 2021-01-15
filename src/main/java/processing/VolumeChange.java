package processing;

import lombok.AllArgsConstructor;
import utils.MathUtils;

@AllArgsConstructor
public class VolumeChange implements Processing {

    private float multiplier;

    @Override
    public float[] apply(float[] buffer) {
        return apply(buffer, 0, buffer.length);
    }

    @Override
    public float[] apply(float[] buffer, int offset, int length) {
        for (int i = offset; i < offset + length; ++i) {
            buffer[i] = MathUtils.clamp(buffer[i] * multiplier, -1f, 1f);
        }
        return buffer;
    }
}
