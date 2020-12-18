package processing;

public interface Processing {
    default float[] apply(float[] buffer) {
        return apply(buffer, 0, buffer.length);
    }
    float[] apply(float[] buffer, int offset, int len);
}
