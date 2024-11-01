package com.example.cattleapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.fxml.FXMLLoader;

public class InsurancePoliciesController {

    @FXML
    private TableView<InsurancePolicy> policyTableView;

    @FXML
    private TableColumn<InsurancePolicy, String> policyNameColumn;

    @FXML
    private TableColumn<InsurancePolicy, Double> premiumAmountColumn;

    @FXML
    private TableColumn<InsurancePolicy, String> coverageDetailsColumn;

    @FXML
    private TextField cattleIdTextField; // TextField for entering cattle ID

    private ObservableList<InsurancePolicy> policyList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Bind table columns to InsurancePolicy properties
        policyNameColumn.setCellValueFactory(new PropertyValueFactory<>("policyName"));
        premiumAmountColumn.setCellValueFactory(new PropertyValueFactory<>("premiumAmount"));
        coverageDetailsColumn.setCellValueFactory(new PropertyValueFactory<>("coverageDetails"));

        // Load the policies from the database when the page is initialized
        loadPolicies();
    }

    private void loadPolicies() {
        // SQL query to fetch all insurance policies from the 'policies' table
        String query = "SELECT * FROM policies";
        try (Connection connection = DatabaseUtil.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            policyList.clear();

            while (resultSet.next()) {
                // Create an InsurancePolicy object for each row in the result set
                InsurancePolicy policy = new InsurancePolicy(
                        resultSet.getInt("policy_id"),
                        resultSet.getString("policy_name"),
                        resultSet.getDouble("premium_amount"),
                        resultSet.getString("coverage_details"),
                        resultSet.getString("breed_eligibility")
                );
                policyList.add(policy);
            }

            // Set the loaded policies into the TableView
            policyTableView.setItems(policyList);
        } catch (Exception e) {
            showAlert("Database Error", "Failed to load insurance policies.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePremiumCalculation() {
        // Get the selected policy from the TableView
        InsurancePolicy selectedPolicy = policyTableView.getSelectionModel().getSelectedItem();
        if (selectedPolicy == null) {
            showAlert("Selection Error", "Please select a policy.");
            return;
        }

        // Get user input from TextField
        String cattleId = cattleIdTextField.getText().trim(); // Get cattle ID input
        if (cattleId.isEmpty()) {
            showAlert("Input Error", "Please provide a cattle ID.");
            return;
        }

        try {
            // Fetch age and breed from the database using cattle ID
            CattleDetails cattleDetails = getCattleDetailsById(cattleId);
            if (cattleDetails == null) {
                showAlert("Input Error", "No cattle found with the given ID.");
                return;
            }

            int age = cattleDetails.getAge(); // Get age from CattleDetails
            String breed = cattleDetails.getBreed(); // Get breed from CattleDetails

            double calculatedPremium = calculatePremium(selectedPolicy, breed, age);

            // Display the calculated premium to the user
            showAlert("Calculated Premium", "The calculated premium for " + selectedPolicy.getPolicyName() + " is: " + calculatedPremium);
        } catch (Exception e) {
            showAlert("Error", "An error occurred while calculating the premium.");
            e.printStackTrace();
        }
    }

    private CattleDetails getCattleDetailsById(String cattleId) {
        // SQL query to fetch cattle details by ID
        String query = "SELECT age, breed FROM cattle WHERE cattle_id = ?";
        try (Connection connection = DatabaseUtil.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, cattleId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new CattleDetails(resultSet.getInt("age"), resultSet.getString("breed")); // Create and return CattleDetails object
            }
        } catch (Exception e) {
            showAlert("Database Error", "Failed to fetch cattle details.");
            e.printStackTrace();
        }
        return null;
    }

    private double calculatePremium(InsurancePolicy policy, String breed, int age) {
        double premium = policy.getPremiumAmount();

        // Check if the breed is eligible for the policy and adjust premium accordingly
        if (policy.getBreedEligibility() != null && policy.getBreedEligibility().contains(breed)) {
            premium *= 0.9; // Apply a 10% discount for eligible breeds
        }

        // Adjust premium based on the age of the cattle
        if (age > 5) { // Assuming 5 is the threshold age for premium increase
            premium *= 1.2; // Increase premium by 20% for older cattle
        }

        return premium;
    }

    @FXML
    private void handlePayment() {
        // Get the selected policy and cattle ID
        InsurancePolicy selectedPolicy = policyTableView.getSelectionModel().getSelectedItem();
        String cattleId = cattleIdTextField.getText().trim();

        if (selectedPolicy == null || cattleId.isEmpty()) {
            showAlert("Error", "Please select a policy and provide a cattle ID.");
            return;
        }

        try (Connection connection = DatabaseUtil.connect()) {
            // Record the premium payment in the premium_payments table
            String insertPaymentQuery = "INSERT INTO premium_payments (cattle_id, policy_id, payment_date) VALUES (?, ?, NOW())";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertPaymentQuery)) {
                preparedStatement.setString(1, cattleId);
                preparedStatement.setInt(2, selectedPolicy.getPolicyId());
                preparedStatement.executeUpdate();
            }

            showAlert("Payment", "Payment for " + selectedPolicy.getPolicyName() + " has been recorded.");
        } catch (Exception e) {
            showAlert("Error", "Failed to record payment.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToDashboard() {
        try {
            // Load the dashboard FXML file
            Parent dashboardView = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
            // Get the current stage and set the new scene
            Stage stage = (Stage) policyTableView.getScene().getWindow();
            stage.setScene(new Scene(dashboardView, 600, 400));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to load the dashboard.");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        // Utility method to show alerts to the user
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class to hold cattle details
    private static class CattleDetails {
        private final int age;
        private final String breed;

        public CattleDetails(int age, String breed) {
            this.age = age;
            this.breed = breed;
        }

        public int getAge() {
            return age;
        }

        public String getBreed() {
            return breed;
        }
    }
}
