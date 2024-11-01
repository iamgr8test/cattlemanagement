package com.example.cattleapplication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRegistrationController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;

    @FXML
    public void goToLogin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CattleApplication.class.getResource("Login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void registerUser() {
        String name = nameField.getText();
        String location = locationField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        try (Connection conn = DatabaseUtil.connect()) {
            String sql = "INSERT INTO users (name, location, username, password) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name);
            pstmt.setString(2, location);
            pstmt.setString(3, username);
            pstmt.setString(4, password);  // Remember to hash passwords in real applications
            pstmt.executeUpdate();

            // Get the generated user ID
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1); // Assuming user ID is an integer
                UserSession.setCurrentUser(username, String.valueOf(userId)); // Set both username and user ID
            }

            System.out.println("User Registered: " + username);

            // Clear fields after registration
            clearFields();

            // Optionally, redirect to login or another page after successful registration
            goToLogin();

        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
        }
    }

    private void clearFields() {
        nameField.clear();
        locationField.clear();
        usernameField.clear();
        passwordField.clear();
    }
}
