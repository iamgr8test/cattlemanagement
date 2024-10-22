package com.example.cattleapplication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Button registerCattleButton; // Reference to the button in FXML

    @FXML
    private Button viewInsurancePoliciesButton; // Reference to the insurance policies button

    @FXML
    private void handleRegisterCattle() {
        // Navigate to cattle registration form
        loadScene("RegisterCattle.fxml", "Register Cattle", 600, 400);
    }

    @FXML
    private void handleViewCattle() {
        // Navigate to view cattle
        loadScene("ViewCattle.fxml", "View Cattle", 800, 600);
    }

    @FXML
    private void handleSubmitClaim() {
        // Navigate to submit insurance claim
        showAlert("Submit Claim", "This feature is not implemented yet.");
    }

    @FXML
    private void handleViewInsuranceStatus() {
        // Navigate to view insurance status
        showAlert("View Insurance Status", "This feature is not implemented yet.");
    }

    @FXML
    private void handleViewInsurancePolicies() {
        // Navigate to insurance policies
        loadScene("InsurancePolicies.fxml", "Insurance Policies", 600, 400);
    }

    @FXML
    private void handleAddInsurancePolicy() {
        // Navigate to add new insurance policy
        loadScene("AddInsurancePolicy.fxml", "Add Insurance Policy", 600, 400);
    }

    @FXML
    private void handleEditInsurancePolicy() {
        // Navigate to edit existing insurance policy
        loadScene("EditInsurancePolicy.fxml", "Edit Insurance Policy", 600, 400);
    }

    @FXML
    private void handleLogout() {
        // Log out and return to the login screen
        loadScene("Login.fxml", "Login", 600, 400);
    }

    private void loadScene(String fxmlFileName, String title, int width, int height) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CattleApplication.class.getResource(fxmlFileName));
            Scene scene = new Scene(fxmlLoader.load(), width, height);
            Stage stage = (Stage) registerCattleButton.getScene().getWindow(); // Get the current window
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the " + title + " view.");
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
