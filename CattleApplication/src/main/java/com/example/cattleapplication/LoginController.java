package com.example.cattleapplication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;  // To display login errors

    @FXML
    private void loginUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // For demonstration, print the username and password
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        // Perform authentication by checking the database
        if (authenticate(username, password)) {
            System.out.println("Login successful!");
            loadDashboard();  // Load the dashboard upon successful login
        } else {
            errorLabel.setText("Invalid username or password.");  // Show error message
        }
    }

    private boolean authenticate(String username, String password) {
        // Query the database to verify user credentials
        try (Connection conn = DatabaseUtil.connect()) {
            String query = "SELECT username FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);  // If password is hashed, use appropriate comparison

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Credentials are valid, retrieve username
                String userId = rs.getString("username");  // Username serves as the user ID
                UserSession.setCurrentUser(username, userId); // Set user session with username and user ID (same)
                return true;
            } else {
                // Invalid credentials
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    private void loadDashboard() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CattleApplication.class.getResource("Dashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openRegistration() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CattleApplication.class.getResource("UserRegistration.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = (Stage) usernameField.getScene().getWindow();  // Get the current stage
            stage.setScene(scene);
            stage.setTitle("User Registration");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to register cattle, ensuring that the user_id corresponds to the logged-in user's username.
     */
    public void registerCattle(String cattleId, String cattleType, String breed, int age, double weight,
                               String healthStatus, String additionalInfo) {
        String currentUsername = UserSession.getCurrentUsername();  // Get the logged-in username
        String userId = UserSession.getCurrentUserId();  // Get the current user's ID (same as username)

        // Insert new cattle into the database
        try (Connection conn = DatabaseUtil.connect()) {
            String query = "INSERT INTO cattle (cattle_id, cattle_type, breed, age, weight, health_status, owner_name, additional_info, user_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, cattleId);
            pstmt.setString(2, cattleType);
            pstmt.setString(3, breed);
            pstmt.setInt(4, age);
            pstmt.setDouble(5, weight);
            pstmt.setString(6, healthStatus);
            pstmt.setString(7, currentUsername);  // Use username as owner_name
            pstmt.setString(8, additionalInfo);
            pstmt.setString(9, userId);  // Set user_id as the current user's username

            pstmt.executeUpdate();
            System.out.println("Cattle registered successfully!");
        } catch (SQLException e) {
            System.out.println("Failed to register cattle: " + e.getMessage());
        }
    }
}
