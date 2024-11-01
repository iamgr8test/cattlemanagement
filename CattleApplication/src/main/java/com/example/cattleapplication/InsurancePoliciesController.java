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
    private TextField cattleIdTextField;

    private ObservableList<InsurancePolicy> policyList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        policyNameColumn.setCellValueFactory(new PropertyValueFactory<>("policyName"));
        premiumAmountColumn.setCellValueFactory(new PropertyValueFactory<>("premiumAmount"));
        coverageDetailsColumn.setCellValueFactory(new PropertyValueFactory<>("coverageDetails"));

        loadPolicies();
    }

    private void loadPolicies() {
        String query = "SELECT * FROM policies";
        try (Connection connection = DatabaseUtil.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            policyList.clear();

            while (resultSet.next()) {
                InsurancePolicy policy = new InsurancePolicy(
                        resultSet.getInt("policy_id"),
                        resultSet.getString("policy_name"),
                        resultSet.getDouble("premium_amount"),
                        resultSet.getString("coverage_details"),
                        resultSet.getString("breed_eligibility")
                );
                policyList.add(policy);
            }

            policyTableView.setItems(policyList);

            logAudit("Load Policies", "Loaded all insurance policies successfully.");
        } catch (Exception e) {
            showAlert("Database Error", "Failed to load insurance policies.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePremiumCalculation() {
        InsurancePolicy selectedPolicy = policyTableView.getSelectionModel().getSelectedItem();
        if (selectedPolicy == null) {
            showAlert("Selection Error", "Please select a policy.");
            return;
        }

        String cattleId = cattleIdTextField.getText().trim();
        if (cattleId.isEmpty()) {
            showAlert("Input Error", "Please provide a cattle ID.");
            return;
        }

        try {
            CattleDetails cattleDetails = getCattleDetailsById(cattleId);
            if (cattleDetails == null) {
                showAlert("Input Error", "No cattle found with the given ID.");
                return;
            }

            int age = cattleDetails.getAge();
            String breed = cattleDetails.getBreed();

            double calculatedPremium = calculatePremium(selectedPolicy, breed, age);

            showAlert("Calculated Premium", "The calculated premium for " + selectedPolicy.getPolicyName() + " is: " + calculatedPremium);

            logAudit("Calculate Premium", "Calculated premium for policy: " + selectedPolicy.getPolicyName() + " for cattle ID: " + cattleId);
        } catch (Exception e) {
            showAlert("Error", "An error occurred while calculating the premium.");
            e.printStackTrace();
        }
    }

    private CattleDetails getCattleDetailsById(String cattleId) {
        String query = "SELECT age, breed FROM cattle WHERE cattle_id = ?";
        try (Connection connection = DatabaseUtil.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, cattleId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new CattleDetails(resultSet.getInt("age"), resultSet.getString("breed"));
            }
        } catch (Exception e) {
            showAlert("Database Error", "Failed to fetch cattle details.");
            e.printStackTrace();
        }
        return null;
    }

    private double calculatePremium(InsurancePolicy policy, String breed, int age) {
        double premium = policy.getPremiumAmount();

        if (policy.getBreedEligibility() != null && policy.getBreedEligibility().contains(breed)) {
            premium *= 0.9;
        }

        if (age > 5) {
            premium *= 1.2;
        }

        return premium;
    }

    @FXML
    private void handlePayment() {
        InsurancePolicy selectedPolicy = policyTableView.getSelectionModel().getSelectedItem();
        String cattleId = cattleIdTextField.getText().trim();

        if (selectedPolicy == null || cattleId.isEmpty()) {
            showAlert("Error", "Please select a policy and provide a cattle ID.");
            return;
        }

        try (Connection connection = DatabaseUtil.connect()) {
            String insertPaymentQuery = "INSERT INTO premium_payments (cattle_id, policy_id, payment_date) VALUES (?, ?, NOW())";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertPaymentQuery)) {
                preparedStatement.setString(1, cattleId);
                preparedStatement.setInt(2, selectedPolicy.getPolicyId());
                preparedStatement.executeUpdate();
            }

            showAlert("Payment", "Payment for " + selectedPolicy.getPolicyName() + " has been recorded.");
            logAudit("Record Payment", "Recorded payment for policy: " + selectedPolicy.getPolicyName() + " for cattle ID: " + cattleId);
        } catch (Exception e) {
            showAlert("Error", "Failed to record payment.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToDashboard() {
        try {
            Parent dashboardView = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
            Stage stage = (Stage) policyTableView.getScene().getWindow();
            stage.setScene(new Scene(dashboardView, 600, 400));
            stage.setTitle("Dashboard");
            stage.show();

            logAudit("Navigate", "Navigated back to the dashboard.");
        } catch (Exception e) {
            showAlert("Error", "Failed to load the dashboard.");
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

    private void logAudit(String actionType, String description) {
        String auditQuery = "INSERT INTO audit_logs (action_type, description, action_timestamp) VALUES (?, ?, NOW())";
        try (Connection connection = DatabaseUtil.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(auditQuery)) {
            preparedStatement.setString(1, actionType);
            preparedStatement.setString(2, description);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.err.println("Audit Logging Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

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
