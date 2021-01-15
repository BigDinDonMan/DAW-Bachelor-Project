package gui.controls;

import effects.SoundEffect;
import gui.controllers.EffectWindowController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.javatuples.Pair;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.reflections.Reflections;
import processing.ChannelSplit;
import processing.Processing;
import providers.SampleProvider;
import utils.SoundClip;
import utils.StringUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;


//TODO: zrobić tak żeby viewery nie mogły na siebie nachodzić i clampować ich pozycję z początkiem/końcem najbliższego viewera (ale może wyskoczyć poza/przed niego)
public class WaveformViewer extends javafx.scene.layout.Pane implements Initializable  {

    public class WaveformSelection {

        private Rectangle selectionRect;
        private ObjectProperty<Paint> selectionColor;
        private ObjectProperty<Boolean> present;

        WaveformSelection() {
            this.selectionRect = new Rectangle();
            this.selectionRect.setOpacity(0.25d);
            this.selectionColor = new SimpleObjectProperty<>(Color.CYAN);
            this.selectionRect.setFill(this.selectionColor.get());
            this.selectionColor.addListener((observable, oldval, newval) -> {
                this.selectionRect.setFill(newval);
            });
            this.present = new SimpleObjectProperty<>(false);
        }

        WaveformSelection(double x, double y, double width, double height, Paint p) {
            this.selectionRect = new Rectangle(x, y, width, height);
            this.selectionRect.setOpacity(0.25d);
            this.selectionColor = new SimpleObjectProperty<>(p);
            this.selectionRect.setFill(this.selectionColor.get());
            this.selectionColor.addListener((observable, oldval, newval) -> {
                this.selectionRect.setFill(newval);
            });
            this.present = new SimpleObjectProperty<>(false);
        }

        public Rectangle getSelectionRect() {
            return this.selectionRect;
        }

        public void setPresent(boolean value) {
            present.setValue(value);
        }

        public boolean isPresent() {
            return present.getValue();
        }

        public void clear() {
            this.selectionRect.setWidth(0d);
            this.present.setValue(false);
        }
    }

    //<editor-fold desc="static stuff">
    private static List<WaveformViewer> viewers;

    public static WaveformViewer getSelected() {
        return viewers.stream().filter(v -> v.selection.isPresent()).findFirst().orElse(null);
    }

    public static void remove(WaveformViewer... items) {
        for (var item: items) {
            viewers.remove(item);
        }
    }

    private static void ensureSingleSelection(WaveformViewer clicked) {
        viewers.forEach(viewer -> {
            if (viewer == clicked) {
                return;
            }
            viewer.selection.clear();
        });
    }

    static {
        viewers = new ArrayList<>();
    }

    public static void clearSelections() {
        viewers.forEach(v -> v.selection.clear());
    }
    //</editor-fold>

    private ContextMenu audioEditContextMenu;

    private int samplesPerPixel = 96;

    private SampleProvider sampleProvider;

    private SoundClip soundClip;

    private Insets waveformPadding;
    private WaveformSelection selection;

    @Deprecated
    public WaveformViewer(SampleProvider provider) {
        super();
        this.sampleProvider = provider;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/controls/WaveformViewer.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public WaveformViewer(SoundClip file) {
        super();
        this.soundClip = file;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/controls/WaveformViewer.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean mouseDragging = false;
    private Point2D mousePosition, startPosition;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        viewers.add(this);
        waveformPadding = new Insets(10, 0, 10, 0);
        if (soundClip != null) {
            setPrefWidth(soundClip.getSamples().length / samplesPerPixel);
            setMinWidth(getPrefWidth());
            selection = new WaveformSelection(getLayoutX(), getLayoutY(), 0, getPrefHeight(), Color.CYAN);
            selection.selectionRect.widthProperty().addListener((observableValue, oldValue, newValue) -> {
                double x = selection.selectionRect.getX();
                double width = (double)newValue;
                if (x + width > getPrefWidth()) {
                    selection.selectionRect.setWidth(getPrefWidth() - x);
                }
            });
            selection.selectionRect.xProperty().addListener((observableValue, oldValue, newValue) -> {
                double newVal = (double)newValue;
                if (newVal < 0d) {
                    selection.selectionRect.setX(0d);
                }
            });
            invalidate();
            getChildren().add(selection.selectionRect);
        }
        setupContextMenu();
        setupEvents();
    }

    public void invalidate() {
        float[] fileBuffer = soundClip.getSamples();
        float min = 0f, max = 0f;

        float[] paintBuffer = new float[samplesPerPixel];
        int offset = 0;

        var children = getChildren();

        for (double x = 0d; x < getPrefWidth(); x += 1d, offset += samplesPerPixel) {
            for (int i = 0; i < (samplesPerPixel > fileBuffer.length - offset ? fileBuffer.length - offset : samplesPerPixel); ++i) {
                paintBuffer[i] = fileBuffer[i + offset] * Short.MAX_VALUE;
                if (paintBuffer[i] > max) max = paintBuffer[i];
                if (paintBuffer[i] < min) min = paintBuffer[i];
            }

            float low, high;
            low = (min - Short.MIN_VALUE) / (Short.MAX_VALUE * 2 - 1);
            high = (max - Short.MIN_VALUE) / (Short.MAX_VALUE * 2 - 1);

            Line l = new Line();
            l.setStartX(x);
            l.setStartY(waveformPadding.getBottom() + low * (getPrefHeight() - waveformPadding.getBottom()));
            l.setEndX(x);
            l.setEndY(high * (getPrefHeight() - waveformPadding.getTop()));
            l.setStroke(Color.BLACK);
            l.setStrokeWidth(2.5d);

            children.add(l);

            min = max = 0f;
        }
    }

    private void setupEvents() {
        addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.isSecondaryButtonDown()) {
                if (selection.isPresent() && !audioEditContextMenu.isShowing()) {
                    audioEditContextMenu.show(this, e.getScreenX(), e.getScreenY());
                } else {
                    audioEditContextMenu.hide();
                    ensureSingleSelection(this);
                    startPosition = new Point2D(e.getX(), e.getY());
                    mousePosition = new Point2D(-1, -1);
                    selection.setPresent(true);
                    mouseDragging = true;
                }
            } else if (e.isPrimaryButtonDown()) {
                ensureSingleSelection(this);
                selection.setPresent(true);
                selection.selectionRect.setX(0d);
                selection.selectionRect.setY(0d);
                selection.selectionRect.setHeight(this.getPrefHeight());
                selection.selectionRect.setWidth(this.getPrefWidth());
            }
        });
        addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if (mouseDragging && e.isSecondaryButtonDown() && !audioEditContextMenu.isShowing()) {
                mouseDragging = false;
                if (Double.compare(mousePosition.getX(), -1) == 0) {
                    double x1, x2;
                    x1 = mousePosition.getX();
                    x2 = mousePosition.getY();
                    if (x2 > x1) {
                        double temp = x1;
                        x1 = x2;
                        x2 = temp;
                    }
                    selection.selectionRect.setX(x2);
                    selection.selectionRect.setY(0d);
                    selection.selectionRect.setWidth(x1 - x2);
                    selection.selectionRect.setHeight(getPrefHeight());
                }
            }
        });
        addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if (e.isSecondaryButtonDown() && !audioEditContextMenu.isShowing()) {
                if (mouseDragging) {
                    if (mousePosition.getX() != -1) {
                        double x1, x2;
                        x1 = mousePosition.getX();
                        x2 = startPosition.getX();
                        if (x2 > x1) {
                            double temp = x2;
                            x2 = x1;
                            x1 = temp;
                        }
                        selection.selectionRect.setX(x2);
                        selection.selectionRect.setY(0d);
                        selection.selectionRect.setWidth(x1 - x2);
                        selection.selectionRect.setHeight(getPrefHeight());
                    }
                    mousePosition = new Point2D(e.getX(), e.getY());
                }
            }
        });
    }

    private AtomicBoolean successfulExit = new AtomicBoolean(true);

    private void setupContextMenu() {
        BiFunction<Class, List<MenuItem>, BiConsumer<String, String>> menuItemMapper = (klass, list) -> (fxmlFilePrefix, fxmlFileSuffix) -> {
            var menuItemNameParts = Arrays.stream(StringUtils.splitUppercase(klass.getSimpleName())).
                    map(String::toLowerCase).
                    toArray(String[]::new);
            menuItemNameParts[0] = org.apache.maven.shared.utils.StringUtils.capitalise(menuItemNameParts[0]);
            var item = new MenuItem(String.join(" ", menuItemNameParts));
            item.setOnAction(e -> {
                var windowName = klass.getSimpleName();
                var filename = "/" + fxmlFilePrefix + windowName + fxmlFileSuffix;
                FXMLLoader loader = new FXMLLoader(getClass().getResource(filename));
                try {
                    Parent p = loader.load();
                    EffectWindowController controller = loader.getController();
                    controller.setAudioClip(
                            soundClip,
                            (int)(selection.getSelectionRect().getX() * samplesPerPixel),
                            (int)((selection.getSelectionRect().getX() + selection.getSelectionRect().getWidth()) * samplesPerPixel)
                    );
                    Scene s = new Scene(p);
                    var style = getClass().getClassLoader().getResource("styles/app-style.css").toExternalForm();
                    s.getStylesheets().addAll(style, BootstrapFX.bootstrapFXStylesheet());
                    Stage stage = new Stage();
                    stage.setScene(s);
                    stage.setTitle(windowName + " properties");
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setOnCloseRequest(event -> successfulExit.set(false));
                    stage.showAndWait();
                    if (successfulExit.get()) {
                        invalidate();
                    }
                    successfulExit.set(true);
                } catch (IOException | IllegalStateException ex) {
                    try {
                        //todo: fix this because we have parameterless sound effect
                        Processing effect = (Processing)klass.getConstructor().newInstance();
                        var newBuffer = effect.apply(soundClip.getSamples());
                        soundClip = new SoundClip(newBuffer, soundClip.getAudioFormat());
                    } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException exc) {
                        exc.printStackTrace();
                    }
                } catch (NullPointerException ex) {
                    new Alert(Alert.AlertType.INFORMATION, "Not implemented yet. Sorry!").showAndWait();
                    return;
                }
            });
            list.add(item);
        };

        Reflections effectReflections = new Reflections("effects");
        Set<Class<? extends SoundEffect>> effectSubtypes = effectReflections.getSubTypesOf(SoundEffect.class);
        audioEditContextMenu = new ContextMenu();

        var clearMenuItem = new MenuItem("Clear selection");
        clearMenuItem.setOnAction(e -> this.selection.clear());

        Menu effectsMenu = new Menu("Effects");
        audioEditContextMenu.getItems().addAll(clearMenuItem, new SeparatorMenuItem(), effectsMenu);

        effectSubtypes.forEach(type -> menuItemMapper.apply(type, effectsMenu.getItems()).accept("effects/", "EffectWindow.fxml"));

        Reflections processingReflections = new Reflections("processing");
        var processingSubtypes = processingReflections.getSubTypesOf(Processing.class);

        Menu processingMenu = new Menu("Processing");
        audioEditContextMenu.getItems().addAll(processingMenu);
        processingSubtypes.forEach(type -> menuItemMapper.apply(type, processingMenu.getItems()).accept("processing/", "Window.fxml"));

        MenuItem splitChannelsMenuItem = new MenuItem("Split channels");
        splitChannelsMenuItem.setOnAction(e -> {
            ChannelSplit splitter = new ChannelSplit(soundClip);
            var channels = splitter.split();
            VBox containersParent = (VBox)this.getParent().getParent();
            for (SoundClip channel : channels) {
                WaveformViewersContainer container = new WaveformViewersContainer();
                WaveformViewer viewer = new WaveformViewer(channel);
                container.addWaveForm(viewer);
                containersParent.getChildren().add(container);
            }
        });
        audioEditContextMenu.getItems().add(splitChannelsMenuItem);
    }

    public void clear() {
        var children = getChildren();
        children.clear();
        children.add(selection.selectionRect);
    }

    public void setSelectionColor(Paint color) {
        selection.selectionColor.setValue(color);
    }

    public SoundClip getSoundClip() {
        return this.soundClip;
    }

    public Optional<Pair<Double, Double>> getSelectionBufferBounds() {
        Pair<Double, Double> rv = null;
        if (selection.isPresent()) {
            rv = new Pair<>(selection.selectionRect.getX(), (selection.selectionRect.getX() + selection.selectionRect.getWidth()));
        }
        return Optional.ofNullable(rv);
    }

    public WaveformSelection getSelection() {
        return this.selection;
    }

    public int getSamplesPerPixel() {
        return this.samplesPerPixel;
    }
}
