package processing;

import javax.sound.sampled.AudioFormat;
import java.util.ArrayList;
import java.util.List;

//to interpolate between numbers; if going from 1 channel to 2 channels, go like this: lerp(a, b, 0), lerp(a, b, 0.5). from 1 to 4 it goes 0, 0,25, 0.5, 0.75 etc.
public class Upsampler implements Processing {

    private AudioFormat currentFormat;
    private AudioFormat targetFormat;

    public Upsampler(AudioFormat curFmt, AudioFormat tarFmt) {
        if (curFmt.getSampleRate() > tarFmt.getSampleRate() || curFmt.getChannels() > tarFmt.getChannels()) {
            throw new IllegalArgumentException("Target format values cannot be lower than current ones");
        }
        this.currentFormat = curFmt;
        this.targetFormat = tarFmt;
    }

    //todo: process each channel separately (sound was repeating because i just took every next sample instead of processing each channel separately)
    @Override
    public float[] apply(float[] buffer, int offset, int len) {
        if (currentFormat.equals(targetFormat))
            return buffer;
//
//        int x = ;
//        int newSamplesSize = buffer.length * x;
//
////        float[] upsampled = new float[newSamplesSize];
//
//        if (currentFormat.getChannels() != targetFormat.getChannels()) {
//            //create a new float array and fill in the channels
//            int channels = targetFormat.getChannels();
//            float[] resampledChannels = new float[targetFormat.getChannels() * buffer.length];
//
//            for (int i = 0, bufferIndex = 0; i < resampledChannels.length; i += channels, ++bufferIndex)
//                for (int j = 0; j < channels; ++j)
//                    resampledChannels[i + j] = buffer[bufferIndex];
//
//            buffer = resampledChannels;
//        }
//
//
//        float value = buffer[0];
//
//        for (int count = 0; count < targetFormat.getChannels(); ++count) {
//
//        }
//
////        for (int i = 0, j = 0; i < upsampled.length; i += x, j++) {
////            upsampled[i] = buffer[j];
////            for (int k = i; k < i + x; k++) {
//////                float current = upsampled[k];
//////                value += (current - value) / (float)targetFormat.getChannels();
//////                upsampled[k] = value;
////            }
////        }

        int targetChannels = targetFormat.getChannels();
        int sampleIncreaseMultiplier = (int)Math.ceil(targetFormat.getSampleRate() / currentFormat.getSampleRate());

        if (currentFormat.getChannels() < targetChannels) {
            float[] withChannels = new float[buffer.length * targetChannels];
            for (int i = 0, bufferIndex = 0; i < withChannels.length; i += targetChannels, ++bufferIndex) {
                for (int j = i; j < i + targetChannels; ++j) {
                    withChannels[j] = buffer[bufferIndex];
                }
            }
            buffer = withChannels;
        }

        float[] newBuffer = new float[sampleIncreaseMultiplier * buffer.length];
        int newBufferIndex = 0;

        for (int currentChannel = 0; currentChannel < targetChannels; ++currentChannel) {
            for (int i = currentChannel; i < buffer.length; i += targetChannels) {

            }
        }

        return null;
    }

}
