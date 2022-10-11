package com.ryl.customControls.other.textfield;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * @author: EFL-ryl
 */
public class ErrorTextField extends VBox {
    @FXML
    private TextField textField;
    @FXML
    private Label errorLabel;

    private boolean autoHide = true;
    private ChangeListener<String> listener = (obs, b, c) -> {
        if (c.contains("error")){
            setError("WARN: SHOW ERROR!!");
        } else {
            hideError();
        }
    };

    @FXML
    private void initialize() {
        textField.textProperty().addListener(listener);
        errorLabel.setStyle("-fx-text-fill: red");
        errorLabel.setWrapText(true);
        hideError();
    }

    /**
     * Définit si l'état d'erreur doit être désactivé lorsque le texte est modifié
     *
     * @param b
     *            true pour désactiver automatiquement
     */
    public void setAutoHide(boolean b) {
        if (b != autoHide) {
            if (b) {
                textField.textProperty().addListener(listener);
            } else {
                textField.textProperty().removeListener(listener);
            }
            autoHide = b;
        }
    }

    /**
     * Active l'etat d'erreur
     *
     * @param error
     *            Le message d'erreur à afficher, une chaîne vide ou null
     *            n'affiche aucun message
     */
    public void setError(String error) {
        textField.setStyle("-fx-border-color: red");
        if (error != null && !error.isEmpty()) {
            errorLabel.setText(error);
            errorLabel.setVisible(true);
        }
    }

    /**
     * Désactive l'état d'erreur
     */
    public void hideError() {
        textField.setStyle("-fx-border-color: transparent");
        errorLabel.setVisible(false);
    }

    /**
     * Renvoie la valeur du champ de texte
     *
     * @return la valeur actuelle du champ de texte
     */
    public String getText() {
        return textField.getText();
    }

    /**
     * Change la valeur du chanp de texte
     *
     * @param text
     *            La nouvelle valeur
     */
    public void setText(String text) {
        textField.setText(text);
    }

    /**
     * Récupère la valeur du prompt
     */
    public String getPromptText() {
        return textField.getPromptText();
    }

    /**
     * Change la valeur du prompt
     */
    public void setPromptText(String text) {
        textField.setPromptText(text);
    }

    /**
     * Fais en sorte que le texte d'erreur retourne à la ligne
     */
    public void setErrorWrapText(boolean wrap) {
        errorLabel.setWrapText(wrap);
    }

    /**
     * Récupère la propriété wrapText du texte d'erreur
     */
    public boolean getErrorWrapText() {
        return errorLabel.isWrapText();
    }

    /**
     * Renvoie le champ de texte
     *
     * @return le champ de texte associé à cet objet
     */
    public TextField getTextField() {
        return textField;
    }
}
