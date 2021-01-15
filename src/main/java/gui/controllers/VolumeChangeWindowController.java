package gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import processing.VolumeChange;
import utils.MathUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class VolumeChangeWindowController extends EffectWindowController implements Initializable {

    @FXML
    private Slider valueIncreaseSlider;

    @FXML
    private TextField percentageDisplayField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        valueIncreaseSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateTextField();
        });

        percentageDisplayField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isBlank()) {
                if (!newValue.matches("\\d*%?$")) {
                    percentageDisplayField.setText(oldValue);
                }
            }
        });

        percentageDisplayField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                updateSlider();
                percentageDisplayField.positionCaret(percentageDisplayField.getText().length());
            }
        });

        percentageDisplayField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                updateSlider();
            }
        });
    }

    private void updateSlider() {
        String input = percentageDisplayField.getText().replace("%", "");
        double enteredValue = Integer.parseInt(input.isBlank() ? "100" : input);
        valueIncreaseSlider.setValue(MathUtils.clamp(enteredValue, valueIncreaseSlider.getMin(), valueIncreaseSlider.getMax()));
        if (!percentageDisplayField.getText().contains("%")) {
            percentageDisplayField.setText(percentageDisplayField.getText() + "%");
        }
    }

    private void updateTextField() {
        double roundedValue = Math.floor(valueIncreaseSlider.getValue() / 5.0d) * 5.0d;
        valueIncreaseSlider.setValue(roundedValue);
        percentageDisplayField.setText(String.format("%.0f%%", roundedValue));
    }

    @FXML
    @Override
    protected void applyEffect() throws Exception {
        VolumeChange changer = new VolumeChange((float)(valueIncreaseSlider.getValue() / 100));
        var newBuffer = changer.apply(clip.getSamples(), bufferStartPoint, bufferEndPoint - bufferStartPoint);
        var samplesField = clip.getClass().getDeclaredField("samples");
        samplesField.setAccessible(true);
        samplesField.set(clip, newBuffer);
        closeWindow();
    }
}
