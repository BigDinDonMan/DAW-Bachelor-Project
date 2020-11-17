import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import javax.sound.sampled.AudioFormat;

public class Preferences {
    private Paint waveformViewerGlobalSelectionColor;

    private static Preferences instance;
    public static Preferences getInstance() {
        return instance;
    }

    static {
        instance = new Preferences();
    }

    private Preferences() {
        waveformViewerGlobalSelectionColor = Color.CYAN;
    }

    public Paint getWaveformViewerGlobalSelectionColor() {
        return waveformViewerGlobalSelectionColor;
    }

    public void setWaveformViewerGlobalSelectionColor(Paint newColor) {
        this.waveformViewerGlobalSelectionColor = newColor;
    }
}
