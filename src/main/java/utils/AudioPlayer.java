package utils;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AudioPlayer{

    private AudioFile audioFile;
    private volatile Clip audioClip;
    private byte[] fileBytes;

    private boolean paused = false;

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

    public AudioPlayer(AudioFile f) {
        this();
        setAudioFile(f);
    }

    public void setAudioFile(AudioFile f) {
        this.audioFile = f;
        fileBytes = convertFileToBytes();
    }

    private byte[] convertFileToBytes() {
        AudioFormat fmt = audioFile.getAudioFormat();
        var samples = audioFile.getSamples();

        var tempBuffer = new byte[samples.length * (fmt.getSampleSizeInBits() / 8)];

        for (int i = 0, bufIndex = 0; i < samples.length; ++i) {
            switch (fmt.getSampleSizeInBits() / 8){
                case 1:
                    byte b = (byte)(samples[i] * Byte.MAX_VALUE);
                    tempBuffer[bufIndex++] = b;
                    break;
                case 2:
                    short s = (short)(samples[i] * Short.MAX_VALUE);
                    ByteBuffer bb = ByteBuffer.allocate(2).order(ByteOrder.nativeOrder());
                    var bytes = bb.putShort(s).array();
                    tempBuffer[bufIndex++] = bytes[0];
                    tempBuffer[bufIndex++] = bytes[1];
                    break;
                case 4:
                    float f = samples[i];
                    ByteBuffer bb_f = ByteBuffer.allocate(4).order(ByteOrder.nativeOrder());
                    var f_bytes = bb_f.putFloat(f).array();
                    for (int j = 0; j < 4; ++j) {
                        tempBuffer[bufIndex++] = f_bytes[j];
                    }
                    break;
            }
        }

        return tempBuffer;
    }

    public AudioFile getAudioFile(){
        return this.audioFile;
    }

    public void play() {
        if (paused) {
            this.audioClip.start();
            paused = false;
            return;
        }
        if (!this.audioClip.isOpen()) {
            try {
                this.audioClip.open(audioFile.getAudioFormat(), fileBytes, 0, fileBytes.length);
                this.audioClip.start();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        }
    }

    public void pause() {
        audioClip.stop();
        paused = true;
    }

    public synchronized void reset() {
        this.audioClip.close();
    }

    public boolean isPaused() {
        return this.paused;
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
