package com.example.cattleapplication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

public class RegisterCattleController {

    @FXML
    private ComboBox<String> cattleTypeField;
    @FXML
    private ComboBox<String> breedField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField weightField;
    @FXML
    private ComboBox<String> healthStatusField;
    @FXML
    private TextField ownerNameField;
    @FXML
    private TextArea additionalInfoField; // New TextArea for additional information

    // Dynamically fetch user ID from UserSession
    private String userId;

    @FXML
    private void initialize() {
        // Get the logged-in user's ID from UserSession
        userId = UserSession.getCurrentUserId(); // Get the actual logged-in user's ID
        System.out.println("Current User ID: " + userId); // Debugging line

        // Populate Cattle Type ComboBox
        List<String> cattleTypes = Arrays.asList("Dairy", "Beef", "Dual-Purpose");
        cattleTypeField.getItems().addAll(cattleTypes);

        // Populate Breed ComboBox based on the selected cattle type
        cattleTypeField.setOnAction(event -> updateBreedOptions());

        // Populate Health Status ComboBox
        List<String> healthStatuses = Arrays.asList("Healthy", "Sick", "Injured", "Quarantined");
        healthStatusField.getItems().addAll(healthStatuses);
    }

    private void updateBreedOptions() {
        String selectedType = cattleTypeField.getValue();
        breedField.getItems().clear(); // Clear previous options

        if (selectedType != null) {
            switch (selectedType) {
                case "Dairy":
                    breedField.getItems().addAll("Jersey", "Holstein", "Guernsey");
                    break;
                case "Beef":
                    breedField.getItems().addAll("Angus", "Hereford", "Charolais");
                    break;
                case "Dual-Purpose":
                    breedField.getItems().addAll("Sahiwal", "Kankrej", "Red Sindhi");
                    break;
                default:
                    breedField.getItems().clear();
                    break;
            }
        }
    }

    @FXML
    private void handleSubmit() {
        String cattleId = generateCattleId(); // Automatically generate cattle ID
        String cattleType = cattleTypeField.getValue();
        String breed = breedField.getValue();
        String age = ageField.getText();
        String weight = weightField.getText();
        String healthStatus = healthStatusField.getValue();
        String ownerName = ownerNameField.getText();
        String additionalInfo = additionalInfoField.getText(); // Get additional information

        // Input validation
        if (cattleType == null || breed == null ||
                age.isEmpty() || weight.isEmpty() || healthStatus == null || ownerName.isEmpty() || userId == null) {
            showAlert("Error", "All fields are required, including user ID!");
            return;
        }

        // Convert age and weight to appropriate types
        int ageInt;
        double weightDouble;
        try {
            ageInt = Integer.parseInt(age);
            weightDouble = Double.parseDouble(weight);
        } catch (NumberFormatException e) {
            showAlert("Error", "Age must be a number and Weight must be a decimal number.");
            return;
        }

        // Save to database
        String insertQuery = "INSERT INTO cattle (cattle_id, cattle_type, breed, age, weight, health_status, owner_name, additional_info, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cattle_management", "root", "Faiza@54321");
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            preparedStatement.setString(1, cattleId);
            preparedStatement.setString(2, cattleType);
            preparedStatement.setString(3, breed);
            preparedStatement.setInt(4, ageInt);  // Set age as integer
            preparedStatement.setDouble(5, weightDouble);  // Set weight as double
            preparedStatement.setString(6, healthStatus);
            preparedStatement.setString(7, ownerName);
            preparedStatement.setString(8, additionalInfo); // Save additional information
            preparedStatement.setString(9, userId);  // Save the user ID dynamically from UserSession

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                showAlert("Success", "Cattle registered successfully!");
                clearFields(); // Clear fields after successful registration

                // Log the cattle registration action in audit logs
                AuditLogger.logAudit("Registered cattle with ID: " + cattleId, userId);
            } else {
                showAlert("Error", "Failed to register cattle.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to register cattle: " + e.getMessage());
        }
    }

    // Method to generate a unique cattle ID
    private String generateCattleId() {
        String cattleId = null;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cattle_management", "root", "Faiza@54321")) {
            String query = "SELECT COUNT(*) AS count FROM cattle";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                cattleId = "C" + (count + 1); // Generate ID like C1, C2, ...
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cattleId;
    }

    @FXML
    private void goBackToDashboard() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CattleApplication.class.getResource("Dashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = (Stage) cattleTypeField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        cattleTypeField.setValue(null);
        breedField.setValue(null);
        ageField.clear();
        weightField.clear();
        healthStatusField.setValue(null);
        ownerNameField.clear();
        additionalInfoField.clear(); // Clear additional information
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
