package gui.controllers;

import effects.Flanger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class FlangerWindowController extends EffectWindowController implements Initializable {

    @FXML
    private TextField sweepFrequencyTextField;
    @FXML
    private TextField delayTextField;
    @FXML
    private TextField sweepRangeTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sweepFrequencyTextField.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            var multipleDotsPresent = sweepFrequencyTextField.getText().chars().filter(c -> c == '.' || c == ',').count() >= 1;
            if (!"-0123456789,.".contains(e.getCharacter()) || (multipleDotsPresent && (e.getCharacter().contains(".") || e.getCharacter().contains(",")))) {
                e.consume();
            }
        });
        delayTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isBlank()) {
                if (!newValue.matches("-?\\d+")) {
                    delayTextField.setText(oldValue);
                }
            }
        });
        sweepRangeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isBlank()) {
                if (!newValue.matches("-?\\d+")) {
                    sweepRangeTextField.setText(oldValue);
                }
            }
        });
    }


    @FXML
    @Override
    protected void applyEffect() {
        float sweepFrequency = Float.parseFloat(sweepFrequencyTextField.getText());
        int sweepRange = Integer.parseInt(sweepRangeTextField.getText());
        int delay = Integer.parseInt(delayTextField.getText());
        Flanger f = new Flanger(getAudioFile().getAudioFormat(), sweepFrequency, delay, sweepRange);
        f.apply(file.getSamples(), bufferStartPoint, bufferEndPoint - bufferStartPoint);
        closeWindow();
    }

}
