package effects;

public interface SoundEffect {
    void apply(float[] buffer);
    void apply(float[] buffer, int offset, int len);
}
