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

    // Standard user login
    @FXML
    private void loginUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (authenticate(username, password)) {
            System.out.println("Login successful!");
            loadDashboard();  // Load the user dashboard upon successful login
        } else {
            errorLabel.setText("Invalid username or password.");
        }
    }

    // Navigate to Admin Login page
    @FXML
    private void loginAdmin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CattleApplication.class.getResource("AdminLogin.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Admin Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean authenticate(String username, String password) {
        // Authenticate user in the database
        try (Connection conn = DatabaseUtil.connect()) {
            String query = "SELECT username FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String userId = rs.getString("username");
                UserSession.setCurrentUser(username, userId);
                return true;
            } else {
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
            stage.setTitle("User Dashboard");
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
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("User Registration");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerCattle(String cattleId, String cattleType, String breed, int age, double weight,
                               String healthStatus, String additionalInfo) {
        String currentUsername = UserSession.getCurrentUsername();
        String userId = UserSession.getCurrentUserId();

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
            pstmt.setString(7, currentUsername);
            pstmt.setString(8, additionalInfo);
            pstmt.setString(9, userId);

            pstmt.executeUpdate();
            System.out.println("Cattle registered successfully!");
        } catch (SQLException e) {
            System.out.println("Failed to register cattle: " + e.getMessage());
        }
    }
}
