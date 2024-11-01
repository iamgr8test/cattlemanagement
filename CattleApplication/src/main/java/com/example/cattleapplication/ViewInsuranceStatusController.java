package com.example.cattleapplication;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ViewInsuranceStatusController {

    @FXML
    private TextField cattleIdTextField; // Input for Cattle ID

    @FXML
    private Label statusLabel; // Label to display claim status

    @FXML
    private void handleCheckStatus() {
        String cattleId = cattleIdTextField.getText(); // Get the cattle ID from the text field

        if (cattleId.isEmpty()) {
            showAlert("Input Error", "Please enter a cattle ID.");
            return;
        }

        // Fetch and display the claim status
        fetchClaimStatus(cattleId);
    }

    private void fetchClaimStatus(String cattleId) {
        String query = "SELECT policy_id, claim_status FROM claims WHERE cattle_id = ?";

        try (Connection connection = DatabaseUtil.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, cattleId);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<String> claimStatusList = new ArrayList<>();

            while (resultSet.next()) {
                String policyId = resultSet.getString("policy_id");
                String claimStatus = resultSet.getString("claim_status");
                claimStatusList.add("Policy ID: " + policyId + " - Claim Status: " + claimStatus);
            }

            if (claimStatusList.isEmpty()) {
                statusLabel.setText("No claims found for the given cattle ID.");
            } else {
                // Join all status messages into a single string for display
                String statusMessage = String.join("\n", claimStatusList);
                statusLabel.setText(statusMessage);
            }

        } catch (Exception e) {
            showAlert("Database Error", "Failed to retrieve the claim status.");
            e.printStackTrace();
        }
    }

    // Handle the back button to navigate to the dashboard
    @FXML
    private void handleBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) cattleIdTextField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to open the dashboard.");
            e.printStackTrace();
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
