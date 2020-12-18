package utils;

public class MathUtils {

    private MathUtils() {}


    public static double clamp(double value, double min, double max) {
        if (value > max) return max;
        if (value < min) return min;
        return value;
    }

    public static int clamp(int value, int min, int max) {
        if (value > max) return max;
        if (value < min) return min;
        return value;
    }

    public static float clamp(float value, float min, float max) {
        if (value > max) return max;
        if (value < min) return min;
        return value;
    }

    public static float lerp(float a, float b, float fraction) {
        return (a * (1f - fraction)) + (b * fraction);
    }
}
