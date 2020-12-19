package processing;

import utils.MathUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

public class Upsampler implements Processing {

    private AudioFormat currentFormat;
    private AudioFormat targetFormat;

    public Upsampler(AudioFormat curFmt, AudioFormat tarFmt) {
        if (!AudioSystem.isConversionSupported(tarFmt, curFmt)) {
            throw new IllegalArgumentException(
                    String.format("Conversion between these formats is not supported. \nSource format: %s\nTarget format: %s",  curFmt.toString(), tarFmt.toString())
            );
        }
        this.currentFormat = curFmt;
        this.targetFormat = tarFmt;
    }

    //todo: process each channel separately (sound was repeating because i just took every next sample instead of processing each channel separately)
    @Override
    public float[] apply(float[] buffer, int offset, int len) {
//        if (currentFormat.equals(targetFormat))
//            return buffer;
////        float value = buffer[0];
////
//////        for (int i = 0, j = 0; i < upsampled.length; i += x, j++) {
//////            upsampled[i] = buffer[j];
//////            for (int k = i; k < i + x; k++) {
////////                float current = upsampled[k];
////////                value += (current - value) / (float)targetFormat.getChannels();
////////                upsampled[k] = value;
//////            }
//////        }
//
//        int targetChannels = targetFormat.getChannels();
//
//        if (currentFormat.getChannels() < targetChannels) {
//            float[] withChannels = new float[buffer.length * targetChannels];
//            for (int i = 0, bufferIndex = 0; i < withChannels.length; i += targetChannels, ++bufferIndex) {
//                for (int j = i; j < i + targetChannels; ++j) {
//                    withChannels[j] = buffer[bufferIndex];
//                }
//            }
//            buffer = withChannels;
//        }
//
//        int outputSize = (int)(buffer.length * (targetFormat.getSampleRate() / currentFormat.getSampleRate()));
//        while (outputSize % targetFormat.getFrameSize() != 0)
//            outputSize++;
//
//        float[] newBuffer = new float[outputSize];
//
//        int multiplier = (int)(Math.ceil(targetFormat.getSampleRate() / currentFormat.getSampleRate()));
//
//        final float interpolationStep = 1f / multiplier;
//
//        for (int channel = 0; channel < targetChannels; ++channel) {
//            for (int i = channel; i < buffer.length - channel - targetChannels; i += targetChannels) {
//                final float currentSample = buffer[i];
//                final float nextSample = buffer[i + targetChannels];
//                float currentFraction = interpolationStep;
//                for (int j = 0, index = i; j < multiplier; ++j, index += targetChannels, currentFraction += interpolationStep) {
//                    newBuffer[index] = MathUtils.lerp(currentSample, nextSample, currentFraction);
//                }
//            }
//        }
//
//        return newBuffer;

        return null;
    }

}
