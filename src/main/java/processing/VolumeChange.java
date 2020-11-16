package processing;

import utils.MathUtils;

public class VolumeChange implements Processing {

    private float multiplier;

    public VolumeChange(float mult) {
        this.multiplier = mult;
    }


    @Override
    public float[] apply(float[] buffer) {
        return apply(buffer, 0, buffer.length);
    }

    @Override
    public float[] apply(float[] buffer, int offset, int length) {
        for (int i = offset; i < offset + length; ++i) {
            buffer[i] *= multiplier;
            buffer[i] = MathUtils.clamp(buffer[i], -1f, 1f);
        }
        return buffer;
    }
}
