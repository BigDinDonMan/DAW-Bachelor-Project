package utils;

import lombok.Getter;
import lombok.Setter;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AudioPlayer{

    @Getter
    private SoundClip soundClip;
    private volatile Clip audioClip;
    private byte[] fileBytes;

    @Getter
    @Setter
    private boolean paused = false;
    @Getter
    @Setter
    private boolean looping = false;

    public AudioPlayer() {
        try {
            audioClip = AudioSystem.getClip();
            audioClip.addLineListener(listener -> {
                var type = listener.getType();
                if (type == LineEvent.Type.STOP) {
                    if (audioClip.getMicrosecondPosition() >= audioClip.getMicrosecondLength()) {
                        reset();
                    }
                }
            });
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    public AudioPlayer(SoundClip f) {
        this();
        setSoundClip(f);
    }

    public void setSoundClip(SoundClip f) {
        this.soundClip = f;
        fileBytes = convertToBytes();
    }

    private byte[] convertToBytes() {
        AudioFormat fmt = soundClip.getAudioFormat();
        var samples = soundClip.getSamples();
        var sampleSize = fmt.getSampleSizeInBits() / 8;
        var tempBuffer = new byte[samples.length * sampleSize];

        for (int i = 0, bufIndex = 0; i < samples.length; ++i) {
            switch (sampleSize){
                case 1:
                    byte b = (byte)(samples[i] * Byte.MAX_VALUE);
                    tempBuffer[bufIndex++] = b;
                    break;
                case 2:
                    short s = (short)(samples[i] * Short.MAX_VALUE);
                    ByteBuffer bb = ByteBuffer.allocate(2).order(fmt.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
                    var bytes = bb.putShort(s).array();
                    tempBuffer[bufIndex++] = bytes[0];
                    tempBuffer[bufIndex++] = bytes[1];
                    break;
                case 4:
                    float f = samples[i];
                    ByteBuffer bb_f = ByteBuffer.allocate(4).order(fmt.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
                    var f_bytes = bb_f.putFloat(f).array();
                    for (int j = 0; j < 4; ++j) {
                        tempBuffer[bufIndex++] = f_bytes[j];
                    }
                    break;
            }
        }

        return tempBuffer;
    }

    public void addPlaybackListener(LineListener e) {
        this.audioClip.addLineListener(e);
    }

    public void play() {
        if (!audioClip.isOpen()) {
            try {
                this.audioClip.open(soundClip.getAudioFormat(), fileBytes, 0, fileBytes.length);
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        }
        if (paused) {
            if (looping) {
                audioClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            audioClip.start();
            paused = false;
        } else {
            if (looping) {
                audioClip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                audioClip.start();
            }
        }
    }

    public void pause() {
        audioClip.stop();
        paused = true;
    }

    public void reset() {
        this.audioClip.stop();
        this.audioClip.setFramePosition(0);
        this.audioClip.setMicrosecondPosition(0);
    }

    public boolean isPlaying() {
        return this.audioClip.isRunning();
    }

    @Override
    protected void finalize() {
        if (this.audioClip != null && this.audioClip.isOpen()) {
            this.audioClip.close();
        }
    }
}
