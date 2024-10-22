package com.example.cattleapplication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button; // Import the Button class
import javafx.scene.Node; // Import Node class for event handling
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Button registerCattleButton; // Reference to the button in FXML

    @FXML
    private void handleRegisterCattle() {
        // Code to navigate to cattle registration form
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CattleApplication.class.getResource("RegisterCattle.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = (Stage) registerCattleButton.getScene().getWindow(); // Get the current window
            stage.setScene(scene);
            stage.setTitle("Register Cattle");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the registration form.");
        }
    }

    @FXML
    private void handleViewCattle() {
        // Code to navigate to view cattle
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CattleApplication.class.getResource("ViewCattle.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600); // Adjust size as needed

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) registerCattleButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("View Cattle");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the cattle view.");
        }
    }

    @FXML
    private void handleSubmitClaim() {
        // Code to navigate to submit insurance claim
        System.out.println("Navigating to Submit Insurance Claim");
        showAlert("Submit Claim", "This feature is not implemented yet.");
    }

    @FXML
    private void handleViewInsuranceStatus() {
        // Code to navigate to view insurance status
        System.out.println("Navigating to View Insurance Status");
        showAlert("View Insurance Status", "This feature is not implemented yet.");
    }

    @FXML
    private void handleLogout() {
        // Code to log out and return to the login screen
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CattleApplication.class.getResource("Login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = (Stage) registerCattleButton.getScene().getWindow(); // Get the current window
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to log out.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
