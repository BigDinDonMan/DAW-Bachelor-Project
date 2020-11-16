package processing;

public interface Processing {
    float[] apply(float[] buffer);
    float[] apply(float[] buffer, int offset, int len);
}
