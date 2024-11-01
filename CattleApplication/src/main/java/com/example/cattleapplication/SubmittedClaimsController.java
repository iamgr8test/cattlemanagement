package com.example.cattleapplication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SubmittedClaimsController {

    @FXML
    private TableView<Claim> submittedClaimsTable;
    @FXML
    private TableColumn<Claim, String> claimIdColumn;
    @FXML
    private TableColumn<Claim, String> cattleIdColumn;
    @FXML
    private TableColumn<Claim, String> claimStatusColumn;
    @FXML
    private TableColumn<Claim, String> submittedByColumn; // Column for user_id (Submitted By)
    @FXML
    private TableColumn<Claim, String> submissionDateColumn; // Column for claim_date (Submission Date)

    @FXML
    public void initialize() {
        // Set cell value factories for each column to bind table columns to Claim properties
        claimIdColumn.setCellValueFactory(new PropertyValueFactory<>("claimId"));
        cattleIdColumn.setCellValueFactory(new PropertyValueFactory<>("cattleId"));
        claimStatusColumn.setCellValueFactory(new PropertyValueFactory<>("claimStatus"));
        submittedByColumn.setCellValueFactory(new PropertyValueFactory<>("userId")); // Bind to user_id property
        submissionDateColumn.setCellValueFactory(new PropertyValueFactory<>("claimDate")); // Bind to claim_date property

        loadSubmittedClaims();
    }

    private void loadSubmittedClaims() {
        try (Connection conn = DatabaseUtil.connect()) {
            String query = "SELECT claim_id, cattle_id, claim_status, user_id, claim_date FROM claims";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            // Fetch data and add it to the table view
            while (rs.next()) {
                String claimId = rs.getString("claim_id");
                String cattleId = rs.getString("cattle_id");
                String claimStatus = rs.getString("claim_status");
                String userId = rs.getString("user_id"); // Get user_id for "Submitted By" column
                String claimDate = rs.getString("claim_date"); // Get claim_date for "Submission Date" column

                submittedClaimsTable.getItems().add(new Claim(claimId, cattleId, claimStatus, userId, claimDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBackToDashboard() {
        try {
            // Load the FXML file for the admin dashboard
            Parent dashboardParent = FXMLLoader.load(getClass().getResource("AdminDashboard.fxml"));
            Scene dashboardScene = new Scene(dashboardParent, 600, 400);

            // Get the current stage and set the new scene
            Stage currentStage = (Stage) submittedClaimsTable.getScene().getWindow();
            currentStage.setScene(dashboardScene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
