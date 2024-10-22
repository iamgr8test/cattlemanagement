package com.example.cattleapplication;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SubmitClaimController {

    @FXML
    private TextField cattleIdField;
    @FXML
    private TextField claimTypeField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField documentPathField;
    @FXML
    private TextField policyIdField;  // Field for policy ID
    @FXML
    private TextField checksumField;  // Field for checksum

    private String userId = "1"; // This should be fetched dynamically for the logged-in user

    @FXML
    private void handleSubmitClaim() {
        String cattleId = cattleIdField.getText();
        String claimType = claimTypeField.getText();
        String description = descriptionField.getText();
        String documentPath = documentPathField.getText();
        String policyId = policyIdField.getText();  // Get policy ID
        String checksum = checksumField.getText();  // Get checksum

        // Validate that cattle exists and belongs to the farmer
        if (!CattleValidator.validateCattleOwnership(cattleId, userId)) {
            showAlert("Error", "You don't own this cattle.");
            return;
        }

        // Validate the insurance policy and checksum
        if (!PolicyValidator.validatePolicy(policyId) || !PolicyValidator.validateChecksum(cattleId, checksum)) {
            showAlert("Error", "Invalid policy or data tampering detected.");
            return;
        }

        // Validate the uploaded document
        if (!DocumentHandler.validateDocumentUpload(documentPath)) {
            showAlert("Error", "Invalid or missing document.");
            return;
        }

        // Save claim to the database
        saveClaimToDatabase(cattleId, claimType, description, documentPath, checksum);

        // Log the action in the audit trail
        AuditLogger.logAction("CLAIM_SUBMISSION", cattleId, "Claim submitted with checksum: " + checksum, userId);

        showAlert("Success", "Claim submitted successfully!");
    }

    private void saveClaimToDatabase(String cattleId, String claimType, String description, String documentPath, String checksum) {
        String insertQuery = "INSERT INTO claims (cattle_id, claim_type, description, document_path, checksum, user_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseUtil.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            preparedStatement.setString(1, cattleId);
            preparedStatement.setString(2, claimType);
            preparedStatement.setString(3, description);
            preparedStatement.setString(4, documentPath);
            preparedStatement.setString(5, checksum);
            preparedStatement.setString(6, userId);  // Save the user ID

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted <= 0) {
                showAlert("Error", "Failed to save the claim to the database.");
            }
        } catch (SQLException e) {
            System.out.println("Error during claim submission: " + e.getMessage());
            showAlert("Error", "Failed to save the claim: " + e.getMessage());
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
