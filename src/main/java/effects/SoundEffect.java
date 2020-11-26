package effects;

public interface SoundEffect {
    default void apply(float[] buffer) {
        apply(buffer, 0, buffer.length);
    }
    void apply(float[] buffer, int offset, int len);
}
