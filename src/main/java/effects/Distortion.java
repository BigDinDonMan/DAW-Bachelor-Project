package effects;

import utils.MathUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//todo: add distortion clipping type (hard clipping, soft clipping, etc.)
//teoretycznie można zrobić abstrakcyjną klasę Distortion i z niej dziedziczyć poszczególne implementacje (soft clipped, hard clipped etc.)

public class Distortion implements SoundEffect {

//    private static final Map<String, Function<Float, Float>> distortionMethods;
//
//    static {
//        distortionMethods = new HashMap<>();
//        distortionMethods.put("soft clip", sample -> 0f);
//        distortionMethods.put("hard clip", sample -> 0f);
//    }

//    private float threshold;
//    private Function<Float, Float> clippingMethod;

//    public Distortion(String method, float threshold) {
////        this.threshold = threshold;
////        this.clippingMethod = distortionMethods.get(method);
//    }

    @Override
    public void apply(float[] buffer, int offset, int len) {
        for (int i = offset; i < offset + len; ++i) {
//            buffer[i] = clippingMethod.apply(buffer[i]);
        }
    }
}
