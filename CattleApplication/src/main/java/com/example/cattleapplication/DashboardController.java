package com.example.cattleapplication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class DashboardController {

    @FXML
    private Button registerCattleButton;

    @FXML
    private Button viewInsurancePoliciesButton;

    @FXML
    private Button submitClaimButton;

    @FXML
    private Button viewInsuranceStatusButton; // Add this line

    @FXML
    private void handleRegisterCattle() {
        loadScene("RegisterCattle.fxml", "Register Cattle", 600, 400);
    }

    @FXML
    private void handleViewCattle() {
        loadScene("ViewCattle.fxml", "View Cattle", 800, 600);
    }

    @FXML
    private void handleSubmitClaim() {
        loadScene("SubmitInsuranceClaim.fxml", "Submit Insurance Claim", 600, 400);
    }

    @FXML
    private void handleViewInsuranceStatus() {
        loadScene("ViewInsuranceStatus.fxml", "View Insurance Status", 600, 400); // Updated line
    }

    @FXML
    private void handleViewInsurancePolicies() {
        loadScene("InsurancePolicies.fxml", "Insurance Policies", 600, 400);
    }

    @FXML
    private void handleAddInsurancePolicy() {
        loadScene("AddInsurancePolicy.fxml", "Add Insurance Policy", 600, 400);
    }

    @FXML
    private void handleEditInsurancePolicy() {
        showAlert("Edit Insurance Policy", "This feature is not implemented yet.");
    }

    @FXML
    private void handleLogout() {
        // Confirm logout
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Are you sure you want to log out?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            loadScene("Login.fxml", "Login", 600, 400);
        }
    }

    private void loadScene(String fxmlFileName, String title, int width, int height) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CattleApplication.class.getResource(fxmlFileName));
            Scene scene = new Scene(fxmlLoader.load(), width, height);
            Stage stage = (Stage) registerCattleButton.getScene().getWindow();
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
