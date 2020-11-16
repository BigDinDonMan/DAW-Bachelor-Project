import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Preferences {
    private int globalSamplesPerPixel;
    private Paint waveformViewerGlobalSelectionColor;

    private static Preferences instance;
    public static Preferences getInstance() {
        return instance;
    }

    static {
        instance = new Preferences();
    }

    private Preferences() {
        globalSamplesPerPixel = 256;
        waveformViewerGlobalSelectionColor = Color.CYAN;
    }


    public int getGlobalSamplesPerPixel() {
        return globalSamplesPerPixel;
    }

    public void setGlobalSamplesPerPixel(int globalSamplesPerPixel) {
        this.globalSamplesPerPixel = globalSamplesPerPixel;
    }

    public Paint getWaveformViewerGlobalSelectionColor() {
        return waveformViewerGlobalSelectionColor;
    }

    public void setWaveformViewerGlobalSelectionColor(Paint newColor) {
        this.waveformViewerGlobalSelectionColor = newColor;
    }
}
