package com.library.frontend.util;

import javafx.scene.control.Alert;

public class AlertUtil {
    public static void showError(String message) {
        show(Alert.AlertType.ERROR, "Error", message);
    }

    public static void showWarning(String message) {
        show(Alert.AlertType.WARNING, "Warning", message);
    }

    private static void show(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
