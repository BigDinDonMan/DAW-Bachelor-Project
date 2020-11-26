package effects;

public class Overdrive implements SoundEffect {
    @Override
    public void apply(float[] buffer, int offset, int len) {

        float threshold = 1f / 3f;
        float doubleThreshold = threshold * 2;

        //this method is called asymmetrical soft clipping
        /*
        * formula is:
        * let threshold = 1/3
        * if abs(sample) < threshold then newSample = 2 * sample
        * if 2*th >= abs(sample) >= th then newSample = (3 - (2 - 3 * sample) ^ 2) / 3f
        * else newSample = 1f
        * */
        for (int i = offset; i < offset + len; ++i) {
            float absoluteSampleValue = Math.abs(buffer[i]);
            if (absoluteSampleValue < threshold) {
                buffer[i] *= 2f;
            } else if (absoluteSampleValue >= threshold && absoluteSampleValue <= doubleThreshold) {
                float newSampleValue = (float)((3 - Math.pow(2 - 3 * absoluteSampleValue, 2)) / 3f);
                buffer[i] = buffer[i] < 0 ? -newSampleValue : newSampleValue;
            } else if (absoluteSampleValue >= doubleThreshold) {
                buffer[i] = buffer[i] < 0 ? -1f : 1f;
            }
        }
    }
}
