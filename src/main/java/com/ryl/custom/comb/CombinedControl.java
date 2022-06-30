package com.ryl.custom.comb;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;

import java.util.Locale;

/**
 * @author Gerrit Grunwald
 * Description: 组合组件类——由TextField和Button组成的自定义实现特定功能的HBox组件,
 * 作用是实现摄氏度和华氏度的值的转换
 */
public class CombinedControl extends HBox {
    private TextField textField;
    private Button button;

    public CombinedControl() {
        init();
        getStyleClass().setAll("combined-control");
        registerListeners();
    }

    private void init() {
        textField = new TextField();
        textField.setFocusTraversable(false);
        textField.setTextFormatter(
                new TextFormatter<>(change -> change.getText().matches("[0-9]*(\\.[0-9]*)?") ? change : null));
        button = new Button("°C");
        button.setFocusTraversable(false);
        setSpacing(0);
        setFillHeight(false);
        setAlignment(Pos.CENTER);
        getChildren().addAll(textField, button);
    }

    private void registerListeners() {
        button.setOnMousePressed(e -> handleControlPropertyChanged("BUTTON_PRESSED"));
    }

    private void handleControlPropertyChanged(final String PROPERTY) {
        if ("BUTTON_PRESSED".equals(PROPERTY)) {
            String buttonText = button.getText();
            String text = textField.getText();
            if (text.matches("^[-+]?\\d+(\\.\\d+)?$")) {
                if ("°C".equals(buttonText)) {
                    // Convert to Fahrenheit
                    button.setText("°F");
                    textField.setText(toFahrenheit(textField.getText()));
                } else {
                    // Convert to Celsius
                    button.setText("°C");
                    textField.setText(toCelsius(textField.getText()));
                }
            }
        }
    }

    private String toFahrenheit(final String text) {
        try {
            double celsius = Double.parseDouble(text);
            return String.format(Locale.US, "%.2f", (celsius * 1.8 + 32));
        } catch (NumberFormatException e) {
            return text;
        }
    }

    private String toCelsius(final String text) {
        try {
            double fahrenheit = Double.parseDouble(text);
            return String.format(Locale.US, "%.2f", ((fahrenheit - 32) / 1.8));
        } catch (NumberFormatException e) {
            return text;
        }
    }
}