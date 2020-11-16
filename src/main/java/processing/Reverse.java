package processing;

import utils.ArrayUtils;

public class Reverse implements Processing {

    @Override
    public float[] apply(float[] buffer) {
        ArrayUtils.reverse(buffer);
        return buffer;
    }

    @Override
    public float[] apply(float[] buffer, int offset, int length) {
        if (offset + length > buffer.length) {
            throw new IllegalArgumentException(
                    String.format(
                            "Offset + length cant be longer than array. Offset = %d, length = %d, buffer length = %d",
                            offset,
                            length,
                            buffer.length
                    )
            );
        }
        ArrayUtils.reverse(buffer, offset, length);
        return buffer;
    }
}
