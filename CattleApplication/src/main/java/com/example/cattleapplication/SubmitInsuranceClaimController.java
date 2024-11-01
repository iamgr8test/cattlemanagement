package com.example.cattleapplication;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SubmitInsuranceClaimController {

    @FXML
    private ComboBox<String> insurancePolicyComboBox; // Dropdown for insurance policies

    @FXML
    private ComboBox<String> cattleIdComboBox; // Dropdown for Cattle IDs

    @FXML
    private TextField claimStatusField; // Text field to input Claim Status

    @FXML
    private void initialize() {
        loadCattleIds(); // Load Cattle IDs into ComboBox
        loadInsurancePolicies(); // Load Insurance Policies into ComboBox
    }

    private void loadCattleIds() {
        // SQL query to fetch cattle IDs of the logged-in user (assumed as 'adi' for now)
        String query = "SELECT cattle_id FROM cattle WHERE user_id = ?";
        try (Connection connection = DatabaseUtil.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "adi"); // Logged-in user
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> cattleIds = new ArrayList<>();

            while (resultSet.next()) {
                cattleIds.add(resultSet.getString("cattle_id"));
            }

            cattleIdComboBox.getItems().addAll(cattleIds); // Populate the ComboBox

        } catch (Exception e) {
            showAlert("Database Error", "Failed to load cattle IDs.");
            e.printStackTrace();
        }
    }

    private void loadInsurancePolicies() {
        // SQL query to fetch all available insurance policies
        String query = "SELECT policy_name FROM policies";
        List<String> policyList = new ArrayList<>();
        try (Connection connection = DatabaseUtil.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                policyList.add(resultSet.getString("policy_name"));
            }

            insurancePolicyComboBox.getItems().addAll(policyList); // Populate the ComboBox

        } catch (Exception e) {
            showAlert("Database Error", "Failed to load insurance policies.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSubmitClaim() {
        String selectedCattleId = cattleIdComboBox.getValue(); // Get selected cattle ID
        String selectedPolicy = insurancePolicyComboBox.getValue(); // Get selected policy name

        if (selectedCattleId == null || selectedPolicy == null) {
            showAlert("Input Error", "Please select both a cattle ID and an insurance policy.");
            return;
        }

        // Logic to submit the insurance claim
        submitClaim(selectedCattleId, selectedPolicy);
    }

    private void submitClaim(String cattleId, String policyName) {
        // SQL query to insert a new claim
        String query = "INSERT INTO claims (cattle_id, policy_id, claim_status) " +
                "VALUES (?, (SELECT policy_id FROM policies WHERE policy_name = ?), 'Submitted')";

        try (Connection connection = DatabaseUtil.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, cattleId);
            preparedStatement.setString(2, policyName);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Success", "Insurance claim submitted successfully.");
            } else {
                showAlert("Error", "Failed to submit the insurance claim.");
            }

        } catch (Exception e) {
            showAlert("Database Error", "Failed to submit the claim: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Handle the back button to navigate to the dashboard
    @FXML
    private void handleBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) insurancePolicyComboBox.getScene().getWindow();
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
